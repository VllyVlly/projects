from tkinter import *

TILE_WIDTH = 50
TILE_HEIGHT = 50

class Tile:
    def __init__(self, x, y, frame):
        self.x=x
        self.y=y
        self.frame = frame
        self.canvas = Canvas(frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        self.canvas.create_rectangle(0, 0, TILE_WIDTH, TILE_HEIGHT, fill="#262b27", outline="")
        self.canvas.grid(row=y, column=x)
        self.fruit = None
        self.snakeBody = None
        self.obstacle = None

    def getFruit(self):
        return self.fruit

    def setFruit(self, fruit):
        self.fruit = fruit

    def setObstacle(self, obstacle):
        self.obstacle = obstacle

    def setSnakeBody(self, snakeBody):
        self.snakeBody = snakeBody

    def hasFruit(self):
        if self.fruit is not None:
            return True
        else:
            return False

    def hasSnakeBody(self):
        if self.snakeBody is not None:
            return True
        else:
            return False

    def hasObstacle(self):
        if self.obstacle is not None:
            return True
        else:
            return False

    def removeFruit(self):
        if self.fruit is not None:
            self.fruit.removeFruit()
            self.fruit = None