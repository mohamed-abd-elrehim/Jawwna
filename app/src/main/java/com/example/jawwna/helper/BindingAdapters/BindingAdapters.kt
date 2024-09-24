import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.jawwna.R

object BindingAdapters {
    @BindingAdapter("app:imageUrl")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        // Check if the URL is not null or empty
        if (!url.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(url)
                .into(view)
        } else {
            // Optionally set a placeholder or default image
            view.setImageResource(R.drawable.ic_favorite_light_mode) // Replace with your placeholder
        }
    }
}
