from ShapeRecognizer import ShapeRecognizer
from Action import *
from PathRecorder import PathRecorder


class PathFollower:
    def __init__(self, drone: Tello):
        self.drone = drone

        self.mov = Movement(self.drone)
        self.recognizer = ShapeRecognizer()
        self.recorder = PathRecorder(self.drone)

        self.actions = {
            "Blue Quad": RecordEnder(self.recorder),
            "Red Quad": RecordStarter(self.recorder),
            "Blue Tri": RightRotation(self.drone),
            "Red Tri": LeftRotation(self.drone),
            "Blue Cir": Podlet(self.mov),
            "Red Cir": Nadlet(self.mov),
            "Yellow Cir": Pristatie(self.drone),
            "Green Cir": ZdvihObete(self.drone),
            "QR": ExecuteQRCode(self.drone, "")
        }

    #this method checks the counters of instance of class ShapeRecognizer and if a threshold gets exceeded,
    #corresponding method of given class gets called
    def check_recognized_shapes(self):
        for s in self.recognizer.recognized_shapes_counters.keys():
            curr = self.recognizer.recognized_shapes_counters[s]
            if curr > 12:
                self.actions[s].vykonaj()
                self.recognizer.recognized_shapes_counters.pop(s)
                self.recognizer.clear_recog_shapes()
                return

    #method to send commands to the drone
    def send_movement(self):
        self.drone.send_rc_control(self.mov.l_r, self.mov.fw_bw, self.mov.up_down, self.mov.rot)

    def set_movement_commands(self, frame):
        #implement your tello line-follower algorithm here



        #code for stopping the drone when ascending/descending
        if self.mov.nadlet or self.mov.podlet:
            self.mov.fw_bw = 0
            self.mov.rot = 0
            self.mov.l_r = 0

        #this code keeps the drone flying at the same altitude if not currently ascending or descending
        if not self.mov.special_action:
            alt = self.drone.get_height()
            needed_pohyb = 100 - alt
            self.mov.up_down = needed_pohyb
