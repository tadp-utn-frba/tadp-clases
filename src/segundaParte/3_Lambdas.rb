# Importante a tener en cuenta en procs
proc = proc { |x| puts x }

proc.call(1)
proc.call(2,3,4,5,6,7,8,9,10) # No importa la cantidad de argumentos que le pase, solo toma el primero

# Importante a tener en cuenta en lambdas
lambda = lambda { |x| puts x }

lambda.call(1)
# lambda.call(2,3,4,5,6,7,8,9,10) # No puedo pasarle mas argumentos de los que espera. Primera diferencia con proc

def m1
  lam = lambda { |x| return x }
  lam.call(1)
  44
end

def m2
  proc = proc { |x| return x }
  proc.call(1)
  44
end

puts m1 # El return de la lambda no bloquea el return de la funcion
puts m2 # El return de la proc hace que no se ejecute el return de la funcion

# Segunda diferencia con proc

