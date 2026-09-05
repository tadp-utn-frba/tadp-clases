puts self
imprimir_self = proc { puts self }




















class A
  def ejecutar_bloque(&bloque)
    bloque.call
  end

  def imprimir_self_proc
    proc { puts self }
  end
end

# un_a = A.new
# puts un_a
# un_a.ejecutar_bloque(&imprimir_self)
# imprimir_self.call
# un_a.ejecutar_bloque(&un_a.imprimir_self_proc)
# un_a.imprimir_self_proc.call
#
# un_a.instance_eval(&imprimir_self)
# un_a.instance_eval(&un_a.imprimir_self_proc)
# self.instance_eval(&imprimir_self)
# self.instance_eval(&un_a.imprimir_self_proc)