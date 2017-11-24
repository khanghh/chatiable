
def func2: Int = {
  println("dfbdfbdfb")
  5
}

def func(a: => Int ): Int = {
  println("456464")
  a
}

func(func2)