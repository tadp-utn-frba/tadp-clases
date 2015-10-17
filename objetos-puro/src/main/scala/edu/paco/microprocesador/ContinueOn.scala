package edu.paco.microprocesador

class ContinueOn(val instruccion: Instruccion) extends RuntimeException

class StopExecution extends RuntimeException
class DivisionByZero extends RuntimeException