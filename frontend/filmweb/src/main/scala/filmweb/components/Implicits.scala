package components.implicits

import com.raquo.laminar.api.L.{*, given}
import upickle.default._
import models.responses._

implicit val rw: Reader[ErrorResponse] = macroR[ErrorResponse]
implicit val owner: Owner = new Owner{} 