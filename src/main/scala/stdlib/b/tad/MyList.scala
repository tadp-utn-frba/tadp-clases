package stdlib.b.tad

sealed trait MyList[T] {
  def isEmpty: Boolean

  def head: T
  def tail: MyList[T]

  def init: MyList[T]
  def last: T

  def add(element: T): MyList[T]
  def append(list: MyList[T]): MyList[T]
}

case class MyLinkedNode[T](data: T, next: MyList[T]) extends MyList[T] {
  def isEmpty = false

  def head: T = data
  
  def tail: MyList[T] = next
  
  def init: MyList[T] = MyLinkedNode(data, next.init)
  
  def last: T = if (next.isEmpty) data else next.last

  def add(element: T): MyList[T] = MyLinkedNode(element, this)
  
  def append(list: MyList[T]): MyList[T] = MyLinkedNode(data, next.append(list))
}

case class MyEmptyNode[T]() extends MyList[T] {
  def isEmpty = true

  def head: T = throw new NoSuchElementException("Empty list")
  
  def tail: MyList[T] = throw new NoSuchElementException("Empty list")

  def init: MyList[T] = throw new NoSuchElementException("Empty list")
  
  def last: T = throw new NoSuchElementException("Empty list")

  def add(element: T): MyList[T] = MyLinkedNode(element, this)
  
  def append(list: MyList[T]): MyList[T] = list
}
