from tkinter import *
import tkinter as tk

TILE_WIDTH = 50
TILE_HEIGHT = 50

class SnakeBody:
    def __init__(self, x, y, frame):
        self.direction = None
        self.prev_direction = None
        self.x = x
        self.y = y
        self.prev_x = None
        self.prev_y = None
        self.frame = frame
        self.canvas = Canvas(frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        self.rectangle = self.canvas.create_rectangle(1, 1, TILE_WIDTH-1, TILE_HEIGHT-1, fill="green", outline="black", width=1)
        self.canvas.grid(row=self.y, column=self.x)

    def setDirection(self, direction):
        self.direction = direction

    def setPrevPosition(self, x, y):
        self.prev_x = x
        self.prev_y = y

    def moveLeft(self):
        self.x -= 1
        if self.checkForEdges():
            self.canvas.grid(row=self.y, column=self.x)

    def moveRight(self):
        self.x += 1
        if self.checkForEdges():
            self.canvas.grid(row=self.y, column=self.x)

    def moveUp(self):
        self.y -= 1
        if self.checkForEdges():
            self.canvas.grid(row=self.y, column=self.x)

    def moveDown(self):
        self.y += 1
        if self.checkForEdges():
            self.canvas.grid(row=self.y, column=self.x)

    def checkForEdges(self):
        if self.x < 0 or self.x >= 10:
            return False
        if self.y < 0 or self.y >= 10:
            return False
        return True

    def putOnTop(self):
        tk.Misc.lift(self.canvas)

    def getX(self):
        return self.x

    def getY(self):
        return self.y

    def getPrevX(self):
        return self.prev_x

    def getPrevY(self):
        return self.prev_y

    def getDirection(self):
        return self.direction

    def positionTo(self, x, y):
        self.canvas.grid(row=y, column=x)
        self.x = x
        self.y = y

    def headIndicator(self):
        self.canvas.delete(self.rectangle)
        self.rectangle = self.canvas.create_rectangle(
            1, 1, 49, 49,
            fill="yellow",
            outline="black",
            width=1
        )
        self.canvas.update_idletasks()