# gems
require 'rubygems'
gem 'gosu'
gem 'aberant-tuio-ruby'
# gem 'rmagick'

# lib
require 'gosu'
require 'tuio_client'
# require 'RMagick'

# app
Dir['**/*.rb'].each { |rb| require rb unless rb == 'init.rb'}

painter_window = PainterWindow.new
TuioWindowAdapter.new(painter_window)

painter_window.show
