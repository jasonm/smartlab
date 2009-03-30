class Surface
  attr_accessor :window

  def initialize(window)
    self.window = window
    @tracked_objects = []
    @cursor = Cursor.new(@window)
  end

  def clicked(button_id)
    @tracked_objects << TrackedObject.new(@window, @window.mouse_x, @window.mouse_y)
  end

  def draw
    @tracked_objects.each { |o| o.draw }
    @cursor.draw
  end

  def update
    @tracked_objects.each(&:update)
  end
end
