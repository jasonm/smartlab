require 'settings'

class SurfaceWindow < Gosu::Window

  def initialize
    super(Settings::ScreenWidth, Settings::ScreenHeight, false)
    self.caption = Settings::Caption
    @surface = Surface.new(self)
  end

  def draw
    @surface.draw
  end

  def update
    @surface.update
  end

  MOUSE_BUTTONS = [Gosu::Button::MsLeft,
                   Gosu::Button::MsRight,
                   Gosu::Button::MsMiddle]

  def button_down(button_id)
    if MOUSE_BUTTONS.include?(button_id)
      @surface.clicked(button_id)
    end

    if button_id == Gosu::Button::KbEscape
      close
    end
  end

end

