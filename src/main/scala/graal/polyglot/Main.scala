package graal.polyglot

import org.graalvm.polyglot._
import org.graalvm.polyglot.proxy._

object Main {
  def main(args: Array[String]): Unit = {
    println("hello scala!!")
    var context: Option[Context] = None
    try {
      context = Some(Context.newBuilder().allowAllAccess(true).build())
      var start = System.currentTimeMillis()
      helloPython(context.get)
      println(s"helloPython elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      helloR(context.get)
      println(s"helloR elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      helloJs(context.get)
      println(s"helloJs elapsed time = ${System.currentTimeMillis() - start}ms")

      start = System.currentTimeMillis()
      println(s"getPythonResult = ${getPythonResult(context.get, 99)}, elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      println(s"getRResult = ${getRResult(context.get, 99)}, elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      println(s"getJsResult = ${getJsResult(context.get, 99)}, elapsed time = ${System.currentTimeMillis() - start}ms")

      start = System.currentTimeMillis()
      println(s"pythonAccessJava = ${pythonAccessJava(context.get)}, elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      println(s"RAccessJava = ${RAccessJava(context.get)}, elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      println(s"JsAccessJava = ${JsAccessJava(context.get)}, elapsed time = ${System.currentTimeMillis() - start}ms")

      start = System.currentTimeMillis()
      println(s"scalaAccessJs = ${scalaAccessJs(context.get)}, elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      println(s"scalaAccessPython = ${scalaAccessPython(context.get)}, elapsed time = ${System.currentTimeMillis() - start}ms")
      start = System.currentTimeMillis()
      println(s"scalaAccessR = ${scalaAccessR(context.get)}, elapsed time = ${System.currentTimeMillis() - start}ms")

      start = System.currentTimeMillis()
      numpyTest(context.get)
      println(s"numpyTest, elapsed time = ${System.currentTimeMillis() - start}ms")
    } catch {
      case t: Throwable => t.printStackTrace()
    } finally {
      if (context.nonEmpty) context.get.close()
    }
  }

  def helloPython(context: Context) = {
    val source = Source.create("python", "print('Hello Python!')")
    context.eval(source)
  }

  def helloR(context: Context) = {
    val source = Source.create("R", "print('Hello R!');")
    context.eval(source)
  }

  def helloJs(context: Context) = {
    val source = Source.create("js", "print('hello javascript!');")
    context.eval(source)
  }

  def getPythonResult(context: Context, n: Integer) = {
    val function = context.eval("python", "lambda x: x + 1")
    //assert(function.canExecute)
    function.execute(n).asInt
  }

  def getRResult(context: Context, n: Integer) = {
    val function = context.eval("R", "function(x) x + 1")
    //assert(function.canExecute)
    function.execute(n).asInt
  }

  def getJsResult(context: Context, n: Integer) = {
    val function = context.eval("js", "x => x+1")
    //assert(function.canExecute)
    function.execute(n).asInt
  }

  def pythonAccessJava(context: Context) = {
    context.getPolyglotBindings.putMember("javaObj", new Param)
    val source = Source.create("python",
      "import polyglot \n" +
        "javaObj = polyglot.import_value('javaObj')\n" +
        //"print('javaObj:', javaObj)\n" +
        "javaObj['id']                == 42" +
        " and javaObj['text']         == '42'" +
        " and javaObj['arr'][1]       == 42" +
        " and javaObj['ret42'].call() == 42")

    context.eval(source).asBoolean()
  }

  def RAccessJava(context: Context) = {
    context.getBindings("R").putMember("javaObj", new Param)
    val source = Source.create("R",
      "    javaObj$id         == 42"   +
        " && javaObj$text       == '42'" +
        " && javaObj$arr[[2]]   == 42"   +
        " && javaObj$ret42()    == 42")

    context.eval(source).asBoolean()
  }

  def JsAccessJava(context: Context) = {
    context.getBindings("js").putMember("javaObj", new Param)
    val source = Source.create("js",
      "    javaObj.id         == 42"          +
        " && javaObj.text       == '42'"        +
        " && javaObj.arr[1]     == 42"          +
        " && javaObj.ret42()    == 42")

    context.eval(source).asBoolean()
  }

  def scalaAccessJs(context: Context) = {
    val result = context.eval("js",
      "({ "                   +
        "id   : 42, "       +
        "text : '42', "     +
        "arr  : [1,42,3] "  +
        "})")
    val id = result.getMember("id").asInt
    val text = result.getMember("text").asString()
    val array = result.getMember("arr").as(classOf[Array[Int]])
    (id, text, array.mkString(" "))
  }

  def scalaAccessPython(context: Context) = {
    val result = context.eval("python",
      "type('obj', (object,), {" +
        "'id'  : 42, "         +
        "'text': '42', "       +
        "'arr' : [1,42,3]"     +
        "})()")
    val id = result.getMember("id").asInt
    val text = result.getMember("text").asString()
    val array = result.getMember("arr").as(classOf[Array[Int]])
    (id, text, array.mkString(" "))
  }

  def scalaAccessR(context: Context) = {
    val result = context.eval("R",
      "list("                +
        "id   = 42, "      +
        "text = '42', "    +
        "arr  = c(1,42,3)" +
        ")")
    val id = result.getMember("id").asInt
    val text = result.getMember("text").asString()
    val array = result.getMember("arr").as(classOf[Array[Int]])
    (id, text, array.mkString(" "))
  }

  def numpyTest(context: Context) = {
    val source = Source.create("python",
      "import numpy as np\n" +
      "perm = np.random.permutation(150)\n" +
      "print(perm)\n"
    )
    context.eval(source)
  }

}

//case class Param(id: Int = 42, text: String = "42", arr: Array[Int] = Array(1,42,3), ret42:() => Int = () => 42)
//class Param(val id: Int = 42, val text: String = "42", val arr: Array[Int] = Array(1,42,3), val ret42:() => Int = () => 42)