# gems
require 'rubygems'
gem 'gosu'
gem 'rmagick'

# lib
require 'gosu'
require 'util'
require 'RMagick'

# app
Dir['**/*.rb'].each { |rb| require rb unless rb == 'init.rb'}

window = SurfaceWindow.new
window.show
