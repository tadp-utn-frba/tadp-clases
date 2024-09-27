










def distance_to(*parametros)
  parametros
    .zip(@tipos, (1..parametros.length))
    .sum do |parametro, tipo, indice|
    indice * distance_for(parametro, tipo)
  end
end

def distance_for(parametro, tipo)
  parametro.class.ancestors.index(tipo)
end
