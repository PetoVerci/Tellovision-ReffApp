import time

import cv2
import math
from pyzbar.pyzbar import decode
from djitellopy import tello, Tello


def flip_frame(frame):
    """
    funkcia na otočenie zrkadleného obrazu, ktorý dron dostal
    :param frame:
    :return:
    """
    return cv2.flip(frame, 0)


def get_frame_width(frame):
    """
    funkcia vráti šírku obrazu
    :param frame:
    :return:
    """
    return frame.shape[1]


def get_frame_height(frame):
    """
    funkcia vráti výšku obrazu
    :param frame:
    :return:
    """
    return frame.shape[0]


def get_largest_contour_of_mask(masked_frame):
    """
    ako argument posielame funkcii binárny obrázok, teda s nejakým konkrétnym thresholdom
    :param masked_frame: frame s aplikovanou maskou
    :return: najvačšia kontúra nachádzajúca sa vo frame
    """
    contours, hierarchy = cv2.findContours(masked_frame, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    if len(contours) != 0:
        return max(contours, key=cv2.contourArea)


def get_bounding_rect_coords(contour):
    """
    funkcia vráti tuple paramentrov x,y,w,h, kde x a y je ľavý horný roh obklopujúceh obldžníka kontúry, w a h sú je šírka a výška
    :param contour:
    :return:
    """
    x, y, w, h = cv2.boundingRect(contour)
    return x, y, w, h


def get_coords_of_drone(frame):
    """

    :param frame: frame
    :return: aktuálna poloha drona, teda stred zorného poľa drona
    """
    return get_frame_width(frame) // 2, get_frame_height(frame) // 2


def get_center_of_contour(contour):
    """
    # funkcia vytvorí ohraničujúci štvoruholník pre zadanú kontúru a vráti jeho stredové hodnoty
    (odporúčam využívať s funkciou get_largest_contour_of_mask)
    :param masked_frame: frame s aplikovanou maskou
    :param contour: žiadaná kontúra
    :return: stredové hodnoty bounding boxu
    """

    x, y, w, h = cv2.boundingRect(contour)
    cx = x + w // 2
    cy = y + h // 2

    return cx, cy


def get_center_coords_of_circle(contour):
    """
    vráti x,y a polomer kruhu zadanej kontúry
    :param contour:
    :return:
    """
    center, radius = cv2.minEnclosingCircle(contour)
    return center[0], center[1], radius


def get_needed_movement_vals(curr_x, curr_y, goal_x, goal_y):
    """
    Funkcia vráti potrebný posun po X-ovej a Y-ovej osi vyjadrený v PIXELOCH :
    kladné X -> posun doprava,
    záporné X -> posun doľava,
    kladné Y -> posun vpred,
    záporné Y -> posun vzad

    :param curr_x: aktuálna X-ová súradnica drona
    :param curr_y: aktuálna Y-ová súradnica drona
    :param goal_x: X-ová súradnica cieľa
    :param goal_y: Y-ová súradnica cieľa
    :return:
    """
    needed_x = (goal_x - curr_x)
    needed_y = (curr_y - goal_y)

    return int(needed_x), int(needed_y)


def get_needed_rotation_angle(x1, y1, x2, y2):
    """
    využitie smerového vektora, arctan, x1 a y1 sú súradnice prvého nenulového bodu bounding rectu, x2 a y2 sú súradnice
    posledného nenulového bodu bounding rectu (pixely sá čítáju zľava doprava od ľavého horného rohu)
    Tento výpočet slúži na aproximovanie potrebného uhla na rotáciu drona. Uhol sa vypočítava od stredu bounding rectu po
    prvý nájdený nenulový pixel.
    :param x1: X-ová súradnica prvého nenulového pixelu v danom frame
    :param y1: Y-ová súradnica prvého nenulového pixelu v danom frame
    :param x2: X-ová súradnica posledného nenulového pixelu v danom frame
    :param y2: Y-ová súradnica posledného nenulového pixelu v danom bounding rectangli
    :return: vráti potrebný uhol natočenia drona (kladná hodnota : rotácia vpravo, záporná hodnota : rotácia vľavo)
    """
    x = x1 - x2
    y = y2 // 2
    angle = math.degrees(math.atan(x / y))
    return int(angle)


def get_begin_end_of_bounding(x, y, w, h, masked_frame):
    """
    parametre reprezentujú konkrétne parametre boundiningRectanglu, teda :
    :param x: X-ko ĺavého horného rohu boudingu
    :param y: Y-ko ĺavého horného rohu boudingu
    :param w: śirka boundingRectu
    :param h: výška boundingRectu
    :param masked_frame: prehliadavaný frame s aplikovanou maskou
    :return: funkcia vráti styri cisla, prvé dve sú koordináty(first_col, first_row) prvého nenulového bodu
    a druhé dve sú koordináty(last_col, last_row) posledného nenulového bodu v binárnom obrázku pre konkrétny ohraničujúci štvoruholník.
    Táto funkcia sa hodí pri zisťovaní smerového vektora pre rotáciu drona.
    """
    first_row, first_col = 0, 0
    last_row, last_col = 0, 0

    for r in range(y, y + h):
        for c in range(x, x + w):
            if masked_frame[r, c]:
                if first_row == 0:
                    first_row, first_col = r, c
                last_row, last_col = r, c

    return first_col, first_row, last_col, last_row


def compare_two_frames(first, second):
    """
    Premení input obrazky do GrayScale verzie, porovná ich a zistí či sú od seba rozličné
    :param first: prvý frame
    :param second: druhý frame
    :return: vráti True ak sú od seba rozličné, inak False
    """
    first_gray = cv2.cvtColor(first, cv2.COLOR_BGR2GRAY)
    seconds_gray = cv2.cvtColor(second, cv2.COLOR_BGR2GRAY)
    diff = cv2.absdiff(first_gray, seconds_gray)
    if cv2.countNonZero(diff) != 0:
        return True
    return False


def decode_qrcode(img):
    """
     Dekóduje QR kód
     :param img: frame
     :return: vráti data ako string
     """
    for code in decode(img):
        data = code.data.decode('utf-8')
        return data


def parse_data(data):
    """
     Pripraví list príkazov letu drona podľa zadefinovaného jazyka v priečinku "syntaxQrJazykaPreDrona.txt.txt"
     :param data: dekódované dáta z QR
     :return: vráti list príkazov pohybu
     """
    parsed_data = data.split("+")
    commands = []
    for i in parsed_data:
        temp = i.split(" ")
        way = temp[0]
        speed = temp[1]
        commands.append((way, speed))
    return commands


def drone_flight_initiate(drone: tello):
    """
    Funkcia ktorou sa dron inicializuje, zapne streaming obrazu a vzlietne
    :param drone: instancia triedy Tello()
    :return:
    """
    drone.connect()
    print(drone.get_battery())
    drone.streamon()
    drone.takeoff()
    drone.send_rc_control(0, 0, 0, 0)
    time.sleep(2)
    drone.send_rc_control(0, 0, 35, 0)
    time.sleep(2.5)
    drone.send_rc_control(0, 0, 0, 0)
    time.sleep(2)


def follow_face(face_center_x, face_center_y, plocha, image, min_accepted_area, max_accepted_area, dron: tello):
    """
    Funkcia pošle dronu príkazy pohybu podľa hodnôt ktoré vypočíta pre úspešné sledovanie tváre.
    :param face_center_x: stredová súradnica tváre na x-ovej osi
    :param face_center_y: stredová súradnica tváre na y-ovej osi
    :param plocha: plocha minimálneho ohraničujúce obĺžnika tváre
    :param image: aktuálny obraz
    :param min_accepted_area: najmenšia akceptovaná plocha minimálneho ohraničujúce obĺžnika tváre,
     pri ktorej nie je potrebný pohyb drona vpred
    :param max_accepted_area: najväčšia akceptovaná plocha minimálneho ohraničujúce obĺžnika tváre,
     pri ktorej nie je potrebný pohyb drona vzad
    :param dron: inštancia drona pre komunikáciu
    :return:
    """
    image_width = get_frame_width(image)
    image_height = image.shape[0]

    forwards_backwards = 0
    needed_x, needed_y = get_needed_movement_vals(image_width // 2, image_height // 2, face_center_x,
                                                  face_center_y)
    needed_x //= 7
    needed_y //= 7
    rotation = get_needed_rotation_angle(face_center_x, face_center_y, image_width // 2, image_height // 2)

    if min_accepted_area < plocha < max_accepted_area:
        forwards_backwards = 0

    elif plocha > max_accepted_area:
        forwards_backwards = -20

    elif plocha < min_accepted_area and plocha != 0:
        forwards_backwards = 20

    if face_center_x == 0:
        rotation = 0
        needed_y = 0
        forwards_backwards = 0

    dron.send_rc_control(0, forwards_backwards, needed_y, rotation)


def find_face(image):
    """
    Funkcia za využitia haarcascade_frontalface_default.xml deteguje tvár v obraze. Ak tvár nájde, vráti 2D zoznam,
    vo forme [coords,area] kde coords je dvojprvkový zoznam integerov a area je typu integer. coords[0] je x-ová súradnica a
    coords[1] y-ová súradnica stredu detegovanej tváre a area je plocha minimálneho ohraničujúceho obdĺžnika
    detegovanej tváre.
    :param image: obraz v ktorom chceme nájsť tvár
    :return: 2D zoznam, vo forme [coords,area] ak je detekcia úpešná, inak [[0,0],0]
    """
    haarcas = cv2.CascadeClassifier(cv2.data.haarcascades + "haarcascade_frontalface_default.xml")
    img_gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = haarcas.detectMultiScale(img_gray, 1.2, 8)

    faces_coords = []

    faces_area = []

    for (x, y, w, h) in faces:
        cv2.rectangle(image, (x, y), (x + w, y + h), (0, 0, 255), 5)

        cx = x + w // 2
        cy = y + h // 2
        curr_face_area = w * h

        cv2.circle(image, (cx, cy), 5, (0, 255, 0), cv2.FILLED)

        faces_coords.append([cx, cy])

        faces_area.append(curr_face_area)

    if len(faces_area) != 0:
        index = faces_area.index(max(faces_area))
        return [faces_coords[index], faces_area[index]]
    else:
        return [[0, 0], 0]
