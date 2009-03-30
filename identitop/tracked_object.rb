class TrackedObject
  def initialize(window, x, y)
    @window, @x, @y = window, x, y
  end

  def draw
    image.draw(@x, @y, 0)
  end

  def update
  end

  private

  def image
    @image ||= begin
      height = 200
      width = 300
      stroke_width = 6

      rimg = Magick::Image.new(width, height) { self.background_color = 'none' }

      gc = Magick::Draw.new
      gc.stroke('white')
      gc.stroke_width(stroke_width)
      gc.fill('none')
      gc.roundrectangle(stroke_width/2, stroke_width/2,
                        width - stroke_width, height - stroke_width,
                        stroke_width*2, stroke_width*2)
      gc.draw(rimg)

      Gosu::Image.new(@window, rimg, false)
    end
  end

end
