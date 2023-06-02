import cv2
import numpy as np

import utils


class Shape:
    def __init__(self):
        self.id = "Abstract"

    def get_angles(self):
        pass

    def is_in_frame(self, masked_frame):
        count_of_angles = self.get_angles()
        contours, hierarchy = cv2.findContours(masked_frame,
                                               cv2.RETR_TREE,
                                               cv2.CHAIN_APPROX_SIMPLE)
        for c in contours:
            area = cv2.contourArea(c)
            if area > 3000:
                peri = cv2.arcLength(c, True)
                approx = cv2.approxPolyDP(c, 0.03 * peri, True)
                pocet_uhlov = len(approx)
                if type(count_of_angles) == range:
                    if pocet_uhlov in count_of_angles:
                        return True

                elif pocet_uhlov == count_of_angles:
                    return True
        return False

    def get_id(self):
        return self.id


class Quadrilateral(Shape):

    def __init__(self):
        super().__init__()
        self.id = "Quad"

    def get_angles(self):
        return 4


class Triangle(Shape):

    def __init__(self):
        super().__init__()
        self.id = "Tri"

    def get_angles(self):
        return 3


class Circle(Shape):
    def __init__(self):
        super().__init__()
        self.id = "Cir"

    def get_angles(self):
        return range(6, 10)


class QRCode:
    def __init__(self):
        self.id = "QR"
        self.data = ""

    def is_in_frame(self, frame):
        curr_data = utils.decode_qrcode(frame)
        if curr_data is not None:
            self.data = curr_data
            return True
        return False



#more to be added ...