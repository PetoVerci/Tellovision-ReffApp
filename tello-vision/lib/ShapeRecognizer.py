from Action import *
from Shape import *

class ShapeRecognizer:

    def __init__(self):
        self.Q = Quadrilateral()
        self.T = Triangle()
        self.C = Circle()
        self.QR = QRCode()

        self.shapes = [self.Q, self.T, self.C, self.QR]

        #to avoid false positive detections, a counter is set for each type of object.
        # When a counter reaches a threshold set by the user, activity is called -> see PathFollower class
        self.recognized_shapes_counters = {
            "Blue Quad": 0,
            "Red Quad": 0,
            "Blue Tri": 0,
            "Blue Cir": 0,
            "Red Cir": 0,
            "Red Tri": 0,
            "Yellow Cir": 0,
            "Green Cir": 0,
            "QR" : 0
        }


    #method that cheks for objects in given frame
    def evaluate_frame(self, frame):
        masks = MaskMaker.make_dict_of_colors(frame)
        for mask in masks:
            for s in self.shapes:
                if s.is_in_frame(masks[mask]):
                    if mask + " " + s.id in self.recognized_shapes_counters.keys():
                        self.recognized_shapes_counters[mask + " " + s.id] += 1



    #side method to set counters of all recognized objects to 0, to be called when one shape is fully detected
    def clear_recog_shapes(self):
        for k in self.recognized_shapes_counters.keys():
            self.recognized_shapes_counters[k] = 0



