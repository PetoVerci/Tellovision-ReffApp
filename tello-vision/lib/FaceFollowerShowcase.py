import cv2.cv2
from djitellopy import Tello
import Action
import utils
from PathRecorder import PathRecorder

drone = Tello()
utils.drone_flight_initiate(drone)
imgold = drone.get_frame_read().frame

rec = PathRecorder(drone, flipped=False)
starter = Action.RecordStarter(rec)
starter.vykonaj()
while True:
    img = drone.get_frame_read().frame
    coords, area = utils.find_face(img)
    utils.follow_face(coords[0], coords[1], area, img, 30000, 60000, drone)
    cv2.imshow("Output", img)
    cv2.waitKey(1)
