from djitellopy import Tello


class Movement:
    def __init__(self, drone: Tello):
        self.l_r = 0
        self.fw_bw = 0
        self.up_down = 0
        self.rot = 0

        self.drone = drone
        self.podlet = False
        self.nadlet = False

        self.special_action = False
