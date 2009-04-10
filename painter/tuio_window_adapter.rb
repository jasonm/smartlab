class TuioWindowAdapter
  def initialize(window)
    @tc = TuioClient.new(:port => 3333)

    @tc.on_object_creation do | to |
      puts "New TUIO Object at x: #{to.x_pos}, y: #{to.y_pos}"
    end

    @tc.on_object_update do | to |
      puts "Updated TUIO Object #{to.fiducial_id} at x: #{to.x_pos}, y: #{to.y_pos}"
      window.update_paintbrush(to.x_pos, to.y_pos)
      Thread.pass # to GOSU
    end

    @tc.on_object_removal do | to |
      puts "Removed TUIO Object #{to.fiducial_id}"
    end

    @tc.on_cursor_creation do | to |
      puts "New TUIO Cursor at x: #{to.x_pos}, y: #{to.y_pos}"
    end

    @tc.on_cursor_update do | to |
      window.update_cursor(to.x_pos, to.y_pos)
      Thread.pass # to GOSU
    end

    @tc.on_cursor_removal do | to |
      puts "Removed TUIO Cursor #{to.session_id}"
    end

    @tc.start
    puts "TUIO init: #{Thread.current.object_id}\t\twith pri #{Thread.current.priority}"
  end
end
