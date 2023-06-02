from enum import IntEnum

#enumeration class for setting wanted altitude of flight of drone
class Vyska(IntEnum):
    NADLET = 170
    PODLET = 70
    REGULAR = 100
    ZODVIHNI_OBET = 40
