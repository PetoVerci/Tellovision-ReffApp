import cv2
import numpy as np


class MaskMaker:
    #erosion and dilation commented in every maskmaking function. After testing masks with and without
    #both erosion and dilation, different lighting conditions may cause unwanted results
    @staticmethod
    def make_red_mask(frame):
        red_lower1 = np.array([0, 90, 90])
        red_upper1 = np.array([10, 255, 255])
        red_lower2 = np.array([170, 100, 100])
        red_upper2 = np.array([180, 255, 255])
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        mask1 = cv2.inRange(hsv, red_lower1, red_upper1)
        mask2 = cv2.inRange(hsv, red_lower2, red_upper2)
        mask = mask1 + mask2
        # mask = cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=3)
        # mask = cv2.dilate(mask, np.ones((5, 5), np.uint8), iterations=3)
        return mask

    @staticmethod
    def make_blue_mask(frame):
        blue_lower = np.array([95, 50, 50])
        blue_upper = np.array([135, 255, 255])
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        mask = cv2.inRange(hsv, blue_lower, blue_upper)
        # mask = cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=1)
        # mask = cv2.dilate(mask, np.ones((5, 5), np.uint8), iterations=1)
        return mask

    @staticmethod
    def make_green_mask(frame):
        green_lower = np.array([45, 75, 71])
        green_upper = np.array([86, 255, 255])
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        mask = cv2.inRange(hsv, green_lower, green_upper)
        # mask = cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=1)
        # mask = cv2.dilate(mask, np.ones((5, 5), np.uint8), iterations=1)
        return mask

    @staticmethod
    def make_yellow_mask(frame):
        yellow_lower = np.array([17, 120, 120])
        yellow_upper = np.array([28, 255, 255])
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        mask = cv2.inRange(hsv, yellow_lower, yellow_upper)
        # mask = cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=1)
        # mask = cv2.dilate(mask, np.ones((5, 5), np.uint8), iterations=1)
        return mask

    @staticmethod
    def make_black_mask(frame):
        lower_black = np.array([0, 0, 0])
        upper_black = np.array([179, 255, 80])
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        mask = cv2.inRange(hsv, lower_black, upper_black)
        # mask = cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=1)
        # mask = cv2.dilate(mask, np.ones((5, 5), np.uint8), iterations=1)
        return mask

    @staticmethod
    def make_dict_of_colors(frame):
        masks = {
            "Red": MaskMaker.make_red_mask(frame),
            "Blue": MaskMaker.make_blue_mask(frame),
            "Green": MaskMaker.make_green_mask(frame),
            "Yellow": MaskMaker.make_yellow_mask(frame)
        }
        return masks
