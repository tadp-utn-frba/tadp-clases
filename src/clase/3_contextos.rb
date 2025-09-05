# frozen_string_literal: true
x = 10

class C1
  puts x

  def m1
    puts x
  end
end


# C2 = Class.new do
#   puts "desde la clase #{x}"
#
#   define_method(:m2) do
#     puts "desde m2 #{x}"
#   end
# end
#
# puts C2.new.m2