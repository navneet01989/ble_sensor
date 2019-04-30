/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
import { StackNavigator } from 'react-navigation';
import DeviceScanScreen from './components/DeviceScanScreen';
import PairingScreen from './components/PairingScreen';


export default App = StackNavigator(
  {
    First: { screen: DeviceScanScreen },
    Second: { screen: PairingScreen }
  });