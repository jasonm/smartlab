class Target
  attr_accessor :shoes, :x, :y, :k

  def initialize(shoes, x, y, k)
    @shoes = shoes
    @x = x
    @y = y
    @k = (k == 1)
  end

  def tick(frame)
  end

  def draw(frame)
    target = self
    raise self.inspect

    @shoes.app do
      raise target.k.inspect

      fill blue
      oval :left   => target.x + Math.sin(var)*10,
           :top    => target.y + Math.cos(var)*10,
           :radius => 100

      fill black
      oval :left   => target.x + Math.sin(var)*10 + 10,
           :top    => target.y + Math.cos(var)*10 + 10,
           :radius => 90
    end
  end
end

