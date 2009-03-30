class Board

  def initialize window, size, position
    @window   = window
    @size     = size
    @position = position
    @tiles    = []
    @blocks   = []
    Settings::ImageBlock.each do |i|
      @blocks << Gosu::Image.new(@window, i, false)
    end
    @width    = @blocks[0].width
    @height   = @blocks[0].height
    randomize
  end

  def clear tiles
    @size.times do |i|
      tiles[i] = []
      @size.times do |j|
        tiles[i][j] = Tile.new @window, false
      end
    end
  end

  def set position, state
    @tiles[position.x][position.y].state = state
  end

  def set_random
    set(Position.new(rand(@size), rand(@size)), true)
  end

  def randomize
    clear @tiles
    (@size * (@size / 2)).times do |i|
      set_random
    end
  end

  def draw
    y = @position.y
    @size.times do |i|
      x = @position.x
      @size.times do |j|
        if @tiles[i][j].state
          @blocks[rand(@blocks.length)].draw x, y, Settings::ZTile
        end
        x = x + @width + Settings::TileBuffer
      end
      y = y + @height + Settings::TileBuffer
    end
  end

  def get_next_state position
    count = count_neighbors(position)
    tile  = @tiles[position.x][position.y]
    if tile.state
      return count == 2 || count == 3
    end
    return count == 3
  end
  
  def check_neighbor i, j
    if i < 0 || i >= @size
      return 0
    end
    if j < 0 || j >= @size
      return 0
    end
    if @tiles[i][j].state
      return 1
    end
    return 0
  end
  
  def count_neighbors position
    i = position.x
    j = position.y
    return  check_neighbor(i - 1, j) +
            check_neighbor(i - 1, j - 1) +
            check_neighbor(i - 1, j + 1) +
            check_neighbor(i, j - 1) +
            check_neighbor(i, j + 1) +
            check_neighbor(i + 1, j) +
            check_neighbor(i + 1, j - 1) +
            check_neighbor(i + 1, j + 1)
  end

  def update
    tiles = []
    clear tiles
    @size.times do |i|
      @size.times do |j|
        tiles[i][j].state = get_next_state(Position.new(i,j))
      end
    end
    @tiles = tiles
  end

end
