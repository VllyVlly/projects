from tkinter import *

TILE_WIDTH = 50
TILE_HEIGHT = 50

class Obstacle:
    def __init__(self, x, y, frame):
        # x and y is position of "center"
        self.x = x
        self.y = y
        self.frame = frame
        self.canvas = None

    def placeL(self):
        block1 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block2 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block3 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block4 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block1.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block2.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block3.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block4.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block1.grid(row=self.y, column=self.x)
        block2.grid(row=self.y, column=self.x+1)
        block3.grid(row=self.y-1, column=self.x)
        block4.grid(row=self.y-2, column=self.x)

    def placeHorizontal(self):
        block1 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block2 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block3 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block1.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block2.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block3.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block1.grid(row=self.y, column=self.x)
        block2.grid(row=self.y, column=self.x + 1)
        block3.grid(row=self.y, column=self.x + 2)

    def placeVertical(self):
        block1 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block2 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block3 = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        block1.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block2.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block3.create_rectangle(0,0,TILE_WIDTH,TILE_HEIGHT,fill="gray", outline="")
        block1.grid(row=self.y, column=self.x)
        block2.grid(row=self.y + 1, column=self.x)
        block3.grid(row=self.y + 2, column=self.x)

    def placeOne(self):
        self.canvas = Canvas(self.frame, width=TILE_WIDTH, height=TILE_HEIGHT, bd=0, highlightthickness=0)
        self.canvas.create_rectangle(0, 0, TILE_WIDTH, TILE_HEIGHT, fill="gray", outline="")
        self.canvas.grid(row=self.y, column=self.x)