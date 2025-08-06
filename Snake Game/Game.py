import random
from tkinter import *
from Tile import Tile
from Snake import SnakeBody
from Fruit import Fruit
from Obstacle import Obstacle
import threading
import time
from timeit import default_timer as timer

startButton = None
Window = None
boardFrame = None
scoreFrame = None

direction = "down"
speed = 0.35
prev_speed = speed
gameOver = False
bodyCounter = 1
cycles = 0
startTime = 0
endTime = 0

body = []
tiles = {}

def loadBoard(gameWindow):
    global boardFrame
    global Window
    global scoreaFrame
    Window = gameWindow
    boardFrame = Frame(gameWindow, width=500, height=500, bg="pink", bd=0)
    boardFrame.place(x=0, y=100)
    makeBoard(boardFrame)

    scoreFrame = Frame(gameWindow, width=500, height=100, bg="blue", bd=1, relief=SUNKEN)
    scoreFrame.place(x=0, y=0)

    scoreLabel = Label(scoreFrame, text="Score: 0")
    scoreLabel.place(x=0, y=40)

    global startButton
    startButton = Button(scoreFrame, width=20, height=5, text="Start", command=startGame)
    startButton.place(relx=0.5, rely=0.5, anchor=CENTER)

    head = SnakeBody(5, 5, boardFrame)
    body.append(head)
    head.headIndicator()

    placeObstacle("L")
    placeObstacle("Horizontal")
    placeObstacle("Vertical")
    placeObstacle("One")
    placeFruit()



    gameWindow.mainloop()

def startGame():
    global startButton
    global boardFrame
    global Window
    global startTime

    Window.bind("<w>", turnUp)
    Window.bind("<s>", turnDown)
    Window.bind("<a>", turnLeft)
    Window.bind("<d>", turnRight)

    if startButton is not None:
        startButton.destroy()

    gameThread = threading.Thread(target=move)
    gameThread.start()
    startTime = startTimer()

def makeBoard(boardFrame):
    for x in range(0,10):
        for y in range(0,10):
            boardTile = Tile(x,y,boardFrame)
            key = f"{y}{x}"
            tiles.update({key: boardTile})

def move():
    global gameOver
    global speed
    global bodyCounter
    global prev_speed
    global cycles
    global scoreFrame

    while not gameOver:
        length = len(body)

        for b in range(length-1,-1,-1):
            if b == 0:
                currentDirection = direction
                x = body[b].getX()
                y = body[b].getY()
                body[b].setPrevPosition(x,y)

                if currentDirection == "left":
                    body[b].moveLeft()
                elif currentDirection == "right":
                    body[b].moveRight()
                elif currentDirection == "up":
                    body[b].moveUp()
                elif currentDirection == "down":
                    body[b].moveDown()

                x = body[b].getX()
                y = body[b].getY()
                key = f"{y}{x}"

                if not body[b].checkForEdges():
                    gameOver = True
                    time.sleep(1)
                    endTimer()
                    endGame("lose")
                    break
                elif checkObstacles(key):
                    gameOver = True
                    time.sleep(1)
                    endTimer()
                    endGame("lose")
                    break
                elif bodyCounter != 1 and tiles.get(key).hasSnakeBody():
                    gameOver = True
                    time.sleep(1)
                    endTimer()
                    endGame("lose")
                    break
                else:
                    if checkFruit(key):
                        placeFruit()
                        if tiles.get(key).getFruit().getFruitType() == "speedUp":
                            speed = 0.15
                        elif tiles.get(key).getFruit().getFruitType() == "speedDown":
                            speed = 0.3
                        elif tiles.get(key).getFruit().getFruitType() == "sizeUp1":
                            addBody()
                            speed = prev_speed
                            bodyCounter = bodyCounter + 1
                        elif tiles.get(key).getFruit().getFruitType() == "sizeUp2":
                            addBody()
                            addBody()
                            speed = prev_speed
                            bodyCounter = bodyCounter + 2
                        if bodyCounter >= 10:
                            gameOver = True
                            time.sleep(1)
                            endTimer()
                            endGame("win")
                            break
                        tiles.get(key).removeFruit()
            else:
                prev_x = body[b-1].getX()
                prev_y = body[b-1].getY()
                x = body[b].getX()
                y = body[b].getY()

                body[b].setPrevPosition(x,y)
                body[b].positionTo(prev_x, prev_y)

        for b in range(length - 1, 0, -1):
            x = body[b].getPrevX()
            y = body[b].getPrevY()
            key = f"{y}{x}"
            tiles.get(key).setSnakeBody(None)

        for b in range(length - 1, 0, -1):
            x = body[b].getX()
            y = body[b].getY()
            key = f"{y}{x}"
            tiles.get(key).setSnakeBody(body[b])

        time.sleep(speed)
        prev_speed -= 0.0005
        cycles += 1
        if cycles == 100:
            cycles = 0
            clearFruit()
            placeFruit()
    print("Game Over")

def clearFruit():
    for x in range(0,10):
        for y in range(0,10):
            key = f"{y}{x}"
            tiles.get(key).removeFruit()

def turnUp(event):
    global direction
    if direction != "down":
        direction = "up"

def turnDown(event):
    global direction
    if direction != "up":
        direction = "down"

def turnLeft(event):
    global direction
    if direction != "right":
        direction = "left"

def turnRight(event):
    global direction
    if direction != "left":
        direction = "right"

def placeFruit():
    global boardFrame
    x = random.randint(0,9)
    y = random.randint(0,9)
    key = str(y) + str(x)

    if tiles.get(key).hasObstacle():
        placeFruit()
    else:
        fruitType = Fruit.pickRandomFruitType()
        fruit = Fruit(x,y,boardFrame, fruitType)
        fruit.load_fruit()
        tiles.get(key).setFruit(fruit)

def placeObstacle(type):
    if type == "L":
        x = random.randint(0,5)
        y = random.randint(2,5)
        obstacle = Obstacle(x,y,boardFrame)
        obstacle.placeL()
        tiles.get(f"{y}{x}").setObstacle(obstacle)
        tiles.get(f"{y}{x+1}").setObstacle(obstacle)
        tiles.get(f"{y-1}{x}").setObstacle(obstacle)
        tiles.get(f"{y-2}{x}").setObstacle(obstacle)
    elif type == "Horizontal":
        x = random.randint(6, 7)
        y = random.randint(0, 9)
        obstacle = Obstacle(x, y, boardFrame)
        obstacle.placeHorizontal()
        tiles.get(f"{y}{x}").setObstacle(obstacle)
        tiles.get(f"{y}{x+1}").setObstacle(obstacle)
        tiles.get(f"{y}{x+2}").setObstacle(obstacle)
    elif type == "Vertical":
        x = random.randint(0, 9)
        y = random.randint(6, 7)
        obstacle = Obstacle(x, y, boardFrame)
        obstacle.placeVertical()
        tiles.get(f"{y}{x}").setObstacle(obstacle)
        tiles.get(f"{y+1}{x}").setObstacle(obstacle)
        tiles.get(f"{y+2}{x}").setObstacle(obstacle)
    else:
        x = random.randint(0, 9)
        y = random.randint(0, 9)
        if tiles.get(f"{y}{x}").hasObstacle():
            placeObstacle("One")
        else:
            obstacle = Obstacle(x, y, boardFrame)
            obstacle.placeOne()
            tiles.get(f"{y}{x}").setObstacle(obstacle)

def checkFruit(key):
    if tiles.get(key).hasFruit():
        return True
    else:
        return False

def checkObstacles(key):
    if tiles.get(key).hasObstacle():
        return True
    else:
        return False

def addBody():
    rearDirection = body[-1].getDirection()
    rearX = body[-1].getX()
    rearY = body[-1].getY()
    newX = rearX
    newY = rearY

    if rearDirection == "left":
        newX = rearX + 1
    elif rearDirection == "right":
        newX = rearX - 1
    elif rearDirection == "up":
        newY = rearY + 1
    elif rearDirection == "down":
        newY = rearY - 1

    key = f"{newY}{newX}"

    snakeBody = SnakeBody(newX,newY,boardFrame)
    snakeBody.setDirection(None)
    snakeBody.putOnTop()
    snakeBody.canvas.update()
    tiles.get(key).setSnakeBody(snakeBody)
    body.append(snakeBody)

def endGame(result):
    global Window, bodyCounter
    resultWindow = Tk()
    resultWindow.title("Game Over")
    resultWindow.resizable(False, False)
    resultWindow.geometry("400x400")
    resultLabel = Label(resultWindow, text="Game Over")
    resultLabel.place(x=200, y=100, anchor=CENTER)
    if result == "win":
        winLabel = Label(resultWindow, text="You Win")
        winLabel.place(x=200, y=200, anchor=CENTER)
        timeLabel = Label(resultWindow, text=endTime)
        timeLabel.place(x=200, y=300, anchor=CENTER)
    else:
        winLabel = Label(resultWindow, text="You Lose")
        winLabel.place(x=200, y=200, anchor=CENTER)
        counter = str(bodyCounter)
        counterLabel = Label(resultWindow, text=f"Score: {counter}")
        counterLabel.place(x=200, y=300, anchor=CENTER)
        timeLabel = Label(resultWindow, text=endTime)
        timeLabel.place(x=200, y=350, anchor=CENTER)
    resultWindow.mainloop()

def startTimer():
    return timer()

def endTimer():
    global endTime, startTime
    end = timer()
    elapsed_time = end - startTime
    minutes = int(elapsed_time // 60)
    seconds = int(elapsed_time % 60)
    endTime = f"{minutes:02d}:{seconds:02d}"
