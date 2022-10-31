import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.presentation.ui.surah.SurahViewModel

class SurahViewModelFactory(private val repositoryImpl: RepositoryImpl) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SurahViewModel(repositoryImpl) as T
    }
}