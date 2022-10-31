import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.presentation.ui.main.MainViewModel

class MainViewModelFactory(private val repositoryImpl: RepositoryImpl) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repositoryImpl) as T
    }
}