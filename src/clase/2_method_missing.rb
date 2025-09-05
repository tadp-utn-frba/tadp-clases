# frozen_string_literal: true
class Logger
  def initialize(obj)
    @obj = obj
  end

  def method_missing(nombre, *args, &block)
    puts "Me llegó el mensaje #{nombre} con los parámetros #{args.inspect}"
    @obj.send(nombre, *args)
  end
end

class Guerrero
  def atacar(otro)
    otro
  end
end

un_logger = Logger.new(Guerrero.new)
puts un_logger.atacar(100)
# puts un_logger.eql?(10)
