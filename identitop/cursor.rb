class Cursor
  def initialize(window)
    @window = window
    @cursor_image = Gosu::Image.new(@window, "media/cursor.png", false)
  end

  def draw
    @cursor_image.draw(@window.mouse_x, @window.mouse_y, 0)
  end
end
