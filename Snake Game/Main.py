from tkinter import *
import Game

mainWindow = Tk()

# Functions
def startUp():
    mainWindow.geometry("500x500")
    mainWindow.title("Snake Game")
    mainWindow.resizable(False,False)

    mainFrame = Frame(mainWindow)
    mainFrame.pack()

    label = Label(mainWindow, text="Snake", font=('Arial', 20, 'bold'), fg="black")
    label.place(x=210, y=100)

    startButton = Button(mainWindow, text="START", fg="black", bg="white", command=startGame, height=2, width=10)
    startButton.place(x=210, y=150)
    exitButton = Button(mainWindow, text="EXIT", fg="black", bg="white", command=exitGame, height=2, width=10)
    exitButton.place(x=210, y=200)
    mainWindow.mainloop()

def startGame():
    gameWindow=Tk()
    gameWindow.geometry("500x600")
    gameWindow.resizable(False,False)
    mainWindow.destroy()
    Game.loadBoard(gameWindow)

def exitGame():
    mainWindow.destroy()

# Main code area
startUp()