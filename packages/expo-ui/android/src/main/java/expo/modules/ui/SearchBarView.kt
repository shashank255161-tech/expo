package expo.modules.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ComposableScope
import expo.modules.kotlin.views.ComposeProps
import expo.modules.kotlin.views.ExpoComposeView

data class SearchBarViewProps(
  val defaultValue: MutableState<String> = mutableStateOf(""),
  val placeholder: MutableState<String> = mutableStateOf(""),
  val leadingIcon: MutableState<String?> = mutableStateOf(null),
  val trailingIcon: MutableState<String?> = mutableStateOf(null),
  val modifiers: MutableState<ModifierList> = mutableStateOf(emptyList())
) : ComposeProps

class SearchBarView(context: Context, appContext: AppContext) :
  ExpoComposeView<SearchBarViewProps>(context, appContext) {
  override val props = SearchBarViewProps()
  private val onValueChanged by EventDispatcher()
  private val onSearchSubmitted by EventDispatcher()
  private val onExpandedChange by EventDispatcher()

  private val queryState = mutableStateOf<String?>(null)
  private val expandedState = mutableStateOf(false)

  var query: String?
    get() = queryState.value
    set(value) {
      queryState.value = value
      onValueChanged(mapOf("value" to (value ?: "")))
    }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun ComposableScope.Content() {
    Box(modifier = Modifier.fillMaxSize()) {
      SearchBar(
        inputField = {
          SearchBarDefaults.InputField(
            query = requireNotNull(queryState.value),
            onQueryChange = {
              queryState.value = it
              onValueChanged(mapOf("value" to it))
            },
            onSearch = {
              onSearchSubmitted(mapOf("value" to it))
              expandedState.value = false
              onExpandedChange(mapOf("expanded" to false))
            },
            expanded = expandedState.value,
            onExpandedChange = {
              expandedState.value = it
              onExpandedChange(mapOf("expanded" to it))
            },
            placeholder = { Text(props.placeholder.value) },
            leadingIcon = {
              if (expandedState.value) {
                IconButton(onClick = {
                  expandedState.value = false
                  onExpandedChange(mapOf("expanded" to false))
                }) {
                  getImageVector("filled.ArrowBack")?.let {
                    Icon(imageVector = it, contentDescription = "Close search")
                  }
                }
              } else {
                val icon = getImageVector(props.leadingIcon.value) ?: getImageVector("filled.Search")
                icon?.let { Icon(imageVector = it, contentDescription = null) }
              }
            },
            trailingIcon = if (expandedState.value && (queryState.value ?: "").isNotEmpty()) {
              {
                IconButton(onClick = {
                  queryState.value = ""
                  onValueChanged(mapOf("value" to ""))
                }) {
                  getImageVector("filled.Close")?.let {
                    Icon(imageVector = it, contentDescription = "Clear query")
                  }
                }
              }
            } else {
              props.trailingIcon.value?.let { iconName ->
                {
                  getImageVector(iconName)?.let { icon ->
                    Icon(imageVector = icon, contentDescription = null)
                  }
                }
              }
            },
          )
        },
        expanded = expandedState.value,
        onExpandedChange = {
          expandedState.value = it
          onExpandedChange(mapOf("expanded" to it))
        },
        modifier = ModifierRegistry.applyModifiers(props.modifiers.value, appContext, this@Content)
      ) {
        Children(ComposableScope())
      }
    }
  }
}
