require 'settings'

class PainterWindow < Gosu::Window

  attr_accessor :cursor_x, :cursor_y

  def initialize
    super(Settings::ScreenWidth, Settings::ScreenHeight, false)
    self.caption = Settings::Caption

    @cursor_x = 0
    @cursor_y = 0
    Thread.current.priority = 2
  end

  def draw
    c = Gosu::Color.new(0xFFFFFFFF)

    draw_line(@cursor_x,      @cursor_y,      c,
              @cursor_x + 10, @cursor_y + 10, c)

    draw_line(@cursor_x + 10, @cursor_y,      c,
              @cursor_x ,     @cursor_y + 10, c)
  end

  def update
    Thread.pass
  end

  def update_paintbrush(x_factor, y_factor)
    @cursor_x = x_factor * width
    @cursor_y = y_factor * height
  end

  ALL_MOUSE_BUTTONS = [Gosu::Button::MsLeft,
                       Gosu::Button::MsRight,
                       Gosu::Button::MsMiddle]

  def button_down(button_id)
    if ALL_MOUSE_BUTTONS.include?(button_id)
      # @surface.clicked(button_id)
    end

    if button_id == Gosu::Button::KbEscape
      close
    end
  end
end
