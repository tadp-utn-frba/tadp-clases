# frozen_string_literal: true
# blocks
puts [1, 2, 3].select { |n| n > 1 }.inspect
puts ([1, 2, 3].select do |n|
  n > 1
end).inspect

def m(&block)
  puts block.call
end

m do
  "Hola"
end

# proc y lambda
p1 = proc { |x| x + 1 }
l1 = lambda { |x| x + 1 }

# p1.call(1)
# p1.call()
#
# l1.call(1)
# l1.call()

def m2
  l = lambda { return "en la lambda" }
  l.call
  "en m2"
end

def m3
  p = proc { return "en el proc" }
  p.call
  "en m3"
end

# transformar block en proc
def call_block(&b)
  b.call
end

# call_block(p1)

# contexto
x = 10
p2 = proc { puts x }
p3 = proc { x = x + 1 }
p2.call

p3.call
p3.call
p3.call
puts x

p4 = proc { puts self }
p4.call
