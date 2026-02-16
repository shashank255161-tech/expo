import { requireNativeView } from 'expo';
import { Ref } from 'react';

import { ExpoModifier, ViewEvent } from '../../types';
import { MaterialIcon } from '../Button/types';

export type SearchBarRef = {
  setText: (newText: string) => Promise<void>;
};

export type SearchBarProps = {
  /**
   * Can be used for imperatively setting text on the SearchBar component.
   */
  ref?: Ref<SearchBarRef>;
  /**
   * Initial value that the SearchBar displays when being mounted. As the SearchBar is an uncontrolled component, change the key prop if you need to change the text value.
   */
  defaultValue?: string;
  /**
   * A callback triggered when the search query text changes.
   */
  onChangeText?: (value: string) => void;
  /**
   * A callback triggered when the user submits a search (presses enter/search on keyboard).
   */
  onSearchSubmitted?: (value: string) => void;
  /**
   * Placeholder text displayed when the search bar is empty.
   */
  placeholder?: string;
  /**
   * Leading Material icon name (e.g., `"filled.Search"`). Defaults to a search icon.
   */
  leadingIcon?: MaterialIcon;
  /**
   * Trailing Material icon name (e.g., `"filled.Close"`).
   */
  trailingIcon?: MaterialIcon;
  /**
   * A callback triggered when the search bar expands or collapses.
   */
  onExpandedChange?: (expanded: boolean) => void;
  /**
   * Content to display when the search bar is expanded (search results).
   */
  children?: React.ReactNode;
  /**
   * Modifiers for the component.
   */
  modifiers?: ExpoModifier[];
};

type NativeSearchBarProps = Omit<SearchBarProps, 'onChangeText' | 'onSearchSubmitted' | 'onExpandedChange'> &
  ViewEvent<'onValueChanged', { value: string }> &
  ViewEvent<'onSearchSubmitted', { value: string }> &
  ViewEvent<'onExpandedChange', { expanded: boolean }>;

const SearchBarNativeView: React.ComponentType<NativeSearchBarProps> = requireNativeView(
  'ExpoUI',
  'SearchBarView'
);

/**
 * @hidden
 */
function transformSearchBarProps(props: SearchBarProps): NativeSearchBarProps {
  return {
    ...props,
    onValueChanged: (event) => {
      props.onChangeText?.(event.nativeEvent.value);
    },
    onSearchSubmitted: props.onSearchSubmitted
      ? (event) => {
          props.onSearchSubmitted?.(event.nativeEvent.value);
        }
      : undefined,
    onExpandedChange: props.onExpandedChange
      ? (event) => {
          props.onExpandedChange?.(event.nativeEvent.expanded);
        }
      : undefined,
  };
}

/**
 * Renders a Material 3 `SearchBar` component.
 */
export function SearchBar(props: SearchBarProps) {
  return <SearchBarNativeView {...transformSearchBarProps(props)} />;
}
