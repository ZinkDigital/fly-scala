package com.zink.scala.fly.stats

object StatsPrinter {
  def writeHeader(beans: Seq[StatsBean]) {
    println("\n====")
    println(pad("Entry Name", nameColumnWidth(beans)) + "Writes\tReads\tTakes\tNotifies")
  }

  def writeStats(beans: Seq[StatsBean]) {
    val columnWidth = nameColumnWidth(beans)
    for (bean ← beans) {
      print(pad(bean.typeName, columnWidth))
      print(bean.writes)
      print("\t" + bean.totalReads)
      print("\t" + bean.totalTakes)
      println("\t" + bean.notifyWriteTmpls + bean.notifyTakeTmpls)
    }
  }

  def pad(s: String, width: Int): String = s + " " * (width - s.length)

  private def nameColumnWidth(beans: Seq[StatsBean]): Int = (0 /: beans) { (m: Int, bean: StatsBean) ⇒ m.max(bean.typeName.length()) } + 4
}
