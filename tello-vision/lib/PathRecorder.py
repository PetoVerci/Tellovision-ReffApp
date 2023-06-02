import time, cv2
from threading import Thread
from djitellopy import Tello


class PathRecorder:

    def __init__(self, tello: Tello, flipped=True):
        self.thread = None
        self.recording = False
        self.tello = tello
        self.fps = 30
        self.thread = Thread(target=self.read_video_stream)
        self.flipped = flipped

    def read_video_stream(self):
        self.tello.streamon()
        frame_input = self.tello.get_frame_read()
        resolution = (960, 720)

        video = cv2.VideoWriter('output.avi',
                                cv2.VideoWriter_fourcc(*'MJPG'),
                                self.fps,
                                resolution)

        while self.recording:
            if self.flipped:
                flipped_frame = cv2.flip(frame_input.frame, 0)
            else:
                flipped_frame = frame_input.frame
            video.write(flipped_frame)
            time.sleep(1 / self.fps)

        video.release()

    def start(self):
        self.recording = True
        self.thread.start()

    def stop(self):
        self.recording = False
        self.thread.join()
