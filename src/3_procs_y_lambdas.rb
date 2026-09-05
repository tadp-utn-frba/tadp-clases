mi_proc = proc { |x| puts x.inspect }
# mi_proc.call()
# mi_proc.call(1)
# mi_proc.call(1, 2)
# puts mi_proc.class
# puts mi_proc

mi_lambda = lambda { |x| puts x.inspect }
# mi_lambda.call()
# mi_lambda.call(1)
# mi_lambda.call(1, 2)
# puts mi_lambda.class
# puts mi_lambda

##################
#
# def m1_proc
#   proc_con_return = proc { return 5 }
#   proc_con_return.call
#   return 10
# end
# puts m1_proc
#
# def m1_lambda
#   lambda_con_return = lambda { return 5 }
#   lambda_con_return.call
#   return 10
# end
# puts m1_lambda