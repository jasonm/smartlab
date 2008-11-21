Shoes.app do

  require 'target'

  @items = []
  10.times do |n|
    @items << Target.new(self, n*10, n*10, n%2)
  end

  animate(30) do |frame|
    @items.each { |item| item.tick(frame) }
    clear
    @items.each { |item| item.draw(frame) }
  end

end
