import threading
import time
import cv2
from djitellopy import Tello
from PathRecorder import PathRecorder
from Movement import Movement
from MaskMaker import MaskMaker
from threading import Thread
import numpy as np
import utils
from Vyska import Vyska


class ExecuteQRCode:
    def __init__(self, dron: Tello, data):
        self.dron = dron
        self.data = data

    def vykonaj(self):
        data = utils.parse_data(self.data)
        speed = 20

        def move(dist, movem: Movement):
            self.dron.send_rc_control(movem.l_r, movem.fw_bw, movem.up_down, movem.rot)
            time.sleep(dist / speed)
            self.dron.send_rc_control(0, 0, 0, 0)
            time.sleep(1)

        for i in data:
            mov = Movement(self.dron)
            direction = i[0]
            distance = i[1]
            if direction == "F":
                mov.fw_bw = speed
            elif direction == "B":
                mov.fw_bw = -speed
            elif direction == "L":
                mov.l_r = -speed
            elif direction == "R":
                mov.l_r = speed
            elif direction == "U":
                mov.up_down = speed
            elif direction == "D":
                mov.up_down = -speed
            elif direction == "RL":
                mov.rot = -speed
            elif direction == "RR":
                mov.rot = speed
            elif direction == "LAND":
                self.dron.land()

            t = Thread(target=move(int(distance), mov))
            t.start()
            t.join()


class RightRotation:

    def __init__(self, dron: Tello):
        self.dron = dron
        self.threadik = Thread(target=self.spinuj)

    def vykonaj(self):
        self.threadik.start()
        self.threadik.join()

    def spinuj(self):
        self.dron.send_rc_control(0, 0, 0, 90)
        time.sleep(4)


class LeftRotation:
    def __init__(self, dron: Tello):
        self.dron = dron
        self.threadik = Thread(target=self.spinuj)

    def vykonaj(self):
        self.threadik.start()
        self.threadik.join()

    def spinuj(self):
        self.dron.send_rc_control(0, 0, 0, -90)
        time.sleep(4)


class RecordStarter:

    def __init__(self, recorder: PathRecorder):
        self.recorder = recorder

    def vykonaj(self):
        if not self.recorder.recording:
            self.recorder.recording = True
            self.recorder.start()


class RecordEnder:

    def __init__(self, recorder: PathRecorder):
        self.recorder = recorder

    def vykonaj(self):
        if self.recorder.recording:
            self.recorder.recording = False
            self.recorder.stop()


class Podlet:
    def __init__(self, mov: Movement):
        self.mov = mov
        self.threadik = threading.Thread(target=self.secondary_thread)
        self.dlzka_podletu = None

    def vykonaj(self):
        self.threadik.start()

    def secondary_thread(self):
        self.mov.podlet = True
        self.mov.special_action = True
        self.dlzka_podletu = 100 // self.mov.fw_bw
        print(self.dlzka_podletu)
        if self.dlzka_podletu > 8:
            self.dlzka_podletu = 7

        self.mov.up_down = -28
        self.mov.fw_bw = 0
        self.mov.rot = 0
        self.mov.l_r = 0
        time.sleep(2)
        self.mov.podlet = False
        self.mov.up_down = 0

        time.sleep(self.dlzka_podletu)
        self.mov.podlet = True
        self.mov.up_down = 25
        self.mov.fw_bw = 0
        self.mov.rot = 0
        self.mov.l_r = 0
        time.sleep(3)
        self.mov.up_down = 0
        self.mov.podlet = False
        self.mov.special_action = False


class Nadlet:
    def __init__(self, mov: Movement):
        self.mov = mov
        self.threadik = threading.Thread(target=self.secondary_thread)
        self.dlzka_nadletu = None

    def vykonaj(self):
        self.threadik.start()

    def secondary_thread(self):
        self.mov.special_action = True
        self.mov.nadlet = True
        self.dlzka_nadletu = 100 // self.mov.fw_bw

        if self.dlzka_nadletu > 8:
            self.dlzka_nadletu = 7

        self.mov.up_down = 33
        self.mov.fw_bw = 0
        self.mov.rot = 0
        self.mov.l_r = 0
        time.sleep(2)

        self.mov.nadlet = False
        self.mov.up_down = 0

        time.sleep(self.dlzka_nadletu)

        self.mov.nadlet = True
        self.mov.up_down = -25
        self.mov.fw_bw = 0
        self.mov.rot = 0
        self.mov.l_r = 0
        time.sleep(3)
        self.mov.up_down = 0
        self.mov.nadlet = False

        self.mov.special_action = False


class ZdvihObete:

    def __init__(self, dron: Tello):
        self.dron = dron

    def vykonaj(self):
        i = 0
        while True:
            curr_frame_orig = self.dron.get_frame_read().frame
            curr_frame_flipped = cv2.flip(curr_frame_orig, 0)
            curr_frame_masked = MaskMaker.make_green_mask(curr_frame_flipped)

            cv2.imshow("pick up view", curr_frame_flipped)
            cv2.waitKey(1)

            biggest = utils.get_largest_contour_of_mask(curr_frame_masked)
            if biggest is not None:
                curr_x, curr_y = utils.get_coords_of_drone(curr_frame_masked)
                goal_x, goal_y, _ = utils.get_center_coords_of_circle(biggest)
                posun_x, posun_y= utils.get_needed_movement_vals(curr_x, curr_y, goal_x, goal_y)
                posun_x = int(np.clip(posun_x, -10, 10))
                posun_y = int(np.clip(posun_y, -10, 10))

                self.dron.send_rc_control(posun_x, posun_y, -10, 0)

                i += 1
                if i > 5:
                    alt = self.dron.get_height()
                    if alt < Vyska.ZODVIHNI_OBET:
                        self.dron.move_up(Vyska.REGULAR - alt)
                        time.sleep(2)
                        return
                    i = 0


class Pristatie:

    def __init__(self, dron: Tello):
        self.dron = dron

    def vykonaj(self):
        while True:
            curr_frame_orig = self.dron.get_frame_read().frame
            curr_frame_flipped = cv2.flip(curr_frame_orig, 0)
            curr_frame_masked = MaskMaker.make_yellow_mask(curr_frame_flipped)

            cv2.imshow("landing view", curr_frame_flipped)
            cv2.waitKey(1)

            biggest = utils.get_largest_contour_of_mask(curr_frame_masked)
            posun_x, posun_y = 0, 0
            if biggest is not None:
                curr_x, curr_y = utils.get_coords_of_drone(curr_frame_masked)
                goal_x, goal_y, _ = utils.get_center_coords_of_circle(biggest)
                posun_x, posun_y = utils.get_needed_movement_vals(curr_x, curr_y, goal_x, goal_y)
                posun_x = posun_x // 15
                posun_y = posun_y // 15
                posun_x = int(np.clip(posun_x, -10, 10))
                posun_y = int(np.clip(posun_y, -10, 10))

            self.dron.send_rc_control(posun_x, posun_y, -10, 0)

            if -4 < posun_x < 4 and -4 < posun_y < 4:
                self.dron.send_rc_control(0, 0, 0, 0)
                time.sleep(3)
                self.dron.streamoff()
                cv2.destroyAllWindows()
                self.dron.land()



#more Actions to be added ...