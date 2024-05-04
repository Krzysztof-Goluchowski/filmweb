package models

sealed trait Category

case object Action extends Category
case object Comedy extends Category
case object Drama extends Category
case object Horror extends Category
case object Thriller extends Category
case object ScienceFiction extends Category

object Category {
  def fromString(string: String): Option[Category] =
    string match {
      case "action" => Some(Action)
      case "comedy" => Some(Comedy)
      case "drama" => Some(Drama)
      case "horror" => Some(Horror)
      case "thriller" => Some(Thriller)
      case "scienceFiction" => Some(ScienceFiction)
    }
}
