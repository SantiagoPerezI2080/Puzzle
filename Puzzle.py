import tkinter as tk
from PIL import Image, ImageTk
import random
import time
import os

class Puzzle(tk.Tk):
    def __init__(self):
        super().__init__()

        self.solution = [(i, j) for i in range(3) for j in range(3)]  # Cambiado a 3x3
        self.buttons = []
        self.last_button = None
        self.start_time = None

        self.init_ui()

    def init_ui(self):
        self.title("Puzzle")

        self.panel = tk.Frame(self, bd=2, relief=tk.SUNKEN)
        self.panel.grid(row=0, column=0)

        try:
            self.source = self.load_image("LavaExpress.jpg")
            self.resized = self.resize_image(self.source, 600)
            self.width, self.height = self.resized.size  # Obtener el ancho y alto de la imagen
        except Exception as e:
            print("Error loading image:", e)
            self.quit()

        for i in range(3):  # Cambiado a 3
            for j in range(3):  # Cambiado a 3
                x, y = j * self.width // 3, i * self.height // 3  # Cambiado a 3
                cropped_image = self.resized.crop((x, y, x + self.width // 3, y + self.height // 3))  # Cambiado a 3
                button = ButtonWithImage(self.panel, cropped_image)
                button.grid(row=i, column=j)
                button.bind("<Button-1>", self.on_button_click)
                button.position = (i, j)
                self.buttons.append(button)
                if i == 2 and j == 2:  # Cambiado a 2
                    self.last_button = button

        random.shuffle(self.buttons)
        self.buttons.append(self.last_button)

        self.start_time = time.time()

    def load_image(self, filename):
        filepath = os.path.join("C:/Juegos/Visual Studio/Projectos/images", filename)
        return Image.open(filepath)

    def resize_image(self, image, width):
        wpercent = width / image.size[0]
        hsize = int(image.size[1] * wpercent)
        return image.resize((width, hsize), Image.BILINEAR)

    def on_button_click(self, event):
        button = event.widget
        lidx = self.buttons.index(self.last_button)
        bidx = self.buttons.index(button)
        if (bidx - 1 == lidx and bidx % 3 != 0) or \
           (bidx + 1 == lidx and lidx % 3 != 0) or \
           (bidx - 3 == lidx) or \
           (bidx + 3 == lidx):
            self.buttons[bidx], self.buttons[lidx] = self.buttons[lidx], self.buttons[bidx]
            self.update_buttons()
            self.check_solution()

    def update_buttons(self):
        for button in self.buttons:
            button.grid_forget()
        for i, button in enumerate(self.buttons):
            button.grid(row=i // 3, column=i % 3)

    def check_solution(self):
        current = [button.position for button in self.buttons]
        if current == self.solution:
            end_time = time.time()
            time_taken = int(end_time - self.start_time)
            tk.messagebox.showinfo("Congratulations", f"Completed in {time_taken} seconds")
            self.quit()

    def greedy_algorithm(self, selected_button):
        lidx = self.buttons.index(self.last_button)
        bidx = self.buttons.index(selected_button)
        possible_moves = []
        if bidx - 1 == lidx and bidx % 3 != 0:
            possible_moves.append(bidx - 1)
        if bidx + 1 == lidx and lidx % 3 != 0:
            possible_moves.append(bidx + 1)
        if bidx - 3 == lidx:
            possible_moves.append(bidx - 3)
        if bidx + 3 == lidx:
            possible_moves.append(bidx + 3)
        
        optimal_move = None
        min_distance = float('inf')
        for move in possible_moves:
            distance = self.manhattan_distance(self.buttons[move].position, self.solution[bidx])
            if distance < min_distance:
                min_distance = distance
                optimal_move = move
        
        if optimal_move is not None:
            self.buttons[bidx], self.buttons[optimal_move] = self.buttons[optimal_move], self.buttons[bidx]
            self.update_buttons()
            self.check_solution()

    def manhattan_distance(self, pos1, pos2):
        return abs(pos1[0] - pos2[0]) + abs(pos1[1] - pos2[1])

class ButtonWithImage(tk.Button):
    def __init__(self, parent, image):
        self.image = ImageTk.PhotoImage(image)
        super().__init__(parent, image=self.image, bd=2, relief=tk.RAISED)
        self.position = None

if __name__ == "__main__":
    app = Puzzle()
    app.mainloop()
