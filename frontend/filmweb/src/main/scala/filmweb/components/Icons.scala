package icons

import com.raquo.laminar.api.L.{*, given}

object Icons {
    def filledStar(): Element = {
        i(
            cls := "material-icons", 
            "star"
        )
    }
    
    def emptyStar(): Element = {
        i(
            cls := "material-icons", 
            "star_border"
        )
    }
    
    def halfStar(): Element = {
        i(
            cls := "material-icons", 
            "star_half"
        )
    }
}