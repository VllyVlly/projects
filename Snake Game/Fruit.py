import random
from tkinter import *

TILE_WIDTH = 50
TILE_HEIGHT = 50
TILE_SIZE = 50
fruitTypes = ["speedUp","speedDown","sizeUp1","sizeUp2"]
r = 15


class Fruit:
    def __init__(self, x, y, frame, fruit_type):
        self.image = None
        self.x = x
        self.y = y
        self.frame = frame
        self.fruit_type = fruit_type
        self.canvas = Canvas(frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0, bg="#262b27")
        self.load_fruit()
        self.canvas.grid(row=self.y, column=self.x)
        self.ui = None

    def load_fruit(self):
        if self.fruit_type == "speedUp":
            self.createFruit(0, 0, "yellow")
        elif self.fruit_type == "speedDown":
            self.createFruit(0, 0, "red")
        elif self.fruit_type == "sizeUp1":
            self.createFruit(0, 0, "green")
        elif self.fruit_type == "sizeUp2":
            self.createFruit(0, 0, "blue")

    def createFruit(self, x, y, color):
        center_x = x * TILE_SIZE + TILE_SIZE // 2
        center_y = y * TILE_SIZE + TILE_SIZE // 2
        x0 = center_x - r
        y0 = center_y - r
        x1 = center_x + r
        y1 = center_y + r
        self.canvas.create_oval(x0, y0, x1, y1, fill=color)

    def removeFruit(self):
        self.canvas.delete('all')
        self.canvas.destroy()

    def getFruitType(self):
        return self.fruit_type

    @staticmethod
    def pickRandomFruitType():
        i = random.randint(0, len(fruitTypes) - 1)
        return fruitTypes[i]