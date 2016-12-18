package io.rdbc.sapi

object Interpolators {

  implicit class SqlInterpolator(val sc: StringContext) extends AnyVal {
    def sql(args: Any*): SqlAndParams = {
      val sqlWithPlaceholders = sc.parts.zipWithIndex.reduce { (t1, t2) =>
        val (part1, idx) = t1
        val (part2, nextIdx) = t2
        (s"$part1:p$idx$part2", nextIdx)
      }._1

      SqlAndParams(sqlWithPlaceholders, args.toVector)
    }
  }

}
