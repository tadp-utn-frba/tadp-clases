def objeto(&definicion_del_objeto)
  objeto_nuevo = Object.new
  objeto_nuevo.instance_eval(&definicion_del_objeto) # se evalua en el contexto de pepe
  objeto_nuevo
end

def clase(&definicion_de_clase)
  clase_nueva = Class.new
  # clase_nueva.instance_eval(&definicion_de_clase) esto no funciona. Van a estar definidos en la singleton class de la clase nueva
  # No puedo acceder pora definir en las singleton class de la clase nueva
  clase_nueva.class_eval(&definicion_de_clase) # ESTO SOLO LO ENTIENDEN LAS CLASES
  clase_nueva
end

# Lo que varia es la target class