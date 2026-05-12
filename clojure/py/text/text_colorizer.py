import tkinter as tk
from tkinter import colorchooser


class Colorizer:
    def __init__(self, root):
        self.root = root
        root.title("Colorizer")
        root.geometry("800x600")

        # 设置左侧输入框
        self.input_text = tk.Text(root, wrap='word', width=40, height=30)
        self.input_text.pack(side=tk.LEFT, padx=10, pady=10)
        self.input_text.bind("<KeyRelease>", self.update_text)

        # 设置右侧输出框
        self.output_text = tk.Text(root, wrap='word', width=40, height=30)
        self.output_text.pack(side=tk.RIGHT, padx=10, pady=10)

        # 设置颜色选择器按钮
        self.color_button = tk.Button(root, text='Choose Color', command=self.choose_color)
        self.color_button.pack(pady=10)

        # 用一个set保存都有哪些颜色
        self.colors = list()

    def choose_color(self):
        selected_text = self.input_text.get(tk.SEL_FIRST, tk.SEL_LAST)
        colors = colorchooser.askcolor()
        if colors and selected_text:
            color = colors[1]  # 取得选择的颜色值
            self.colors.append(color)
            self.input_text.tag_config(color, foreground=color)
            self.input_text.tag_add(color, tk.SEL_FIRST, tk.SEL_LAST)

            self.update_text()

    def update_text(self, event=None):
        # 清空输出框
        self.output_text.config(state=tk.NORMAL)
        self.output_text.delete('1.0', tk.END)

        # 处理文本
        input_text = self.input_text.get('1.0', tk.END)
        output_text = input_text.replace('\n', '|n')

        # 将处理后的文本输出到输出框
        self.output_text.insert('1.0', output_text)
        self.output_text.config(state=tk.DISABLED)


if __name__ == '__main__':
    root = tk.Tk()
    app = Colorizer(root)
    root.mainloop()
