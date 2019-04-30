import React, { Component } from 'react';
import {
  Platform,
  PermissionsAndroid,
  Text,
  View,
  StyleSheet,
  FlatList,
  TouchableHighlight
} from 'react-native';
import { List } from "react-native-elements";
import Toast, {DURATION} from 'react-native-easy-toast'
import { BleManager } from 'react-native-ble-plx';

export default class DeviceScanScreen extends React.Component {
  constructor(props) {
    super(props)
    console.disableYellowBox = true;
    if (Platform.OS === 'ios') {
      this.manager = new BleManager({restoreStateIdentifier: 'testBleBackgroundMode', restoreStateFunction: bleRestoredState => {}});
    } else {
      this.manager = new BleManager();
    }
    this.state = {
      info: "",
      values: {},
      busy: false,
      deviceList: []
    }
  }
  info(message) {
    this.setState({info: message})
  }

  error(message) {
    this.setState({info: "ERROR: " + message})
  }
  componentWillUnmount() {

  }
  componentDidMount() {
    if (Platform.OS === 'ios') {
      this.manager.onStateChange((state) => {
        if (state === 'PoweredOn') this.scanAndConnect()
      })
    } else {
      this.scanAndConnect()
    }
    if (Platform.OS === 'android' && Platform.Version >= 23) {
      PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION).then((result) => {
          if (result) {
            console.log("Permission is OK");
          } else {
            PermissionsAndroid.requestPermission(PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION).then((result) => {
              if (result) {
                console.log("User accept");
              } else {
                console.log("User refuse");
              }
            });
          }
      });
    }
  }
  scanAndConnect() {
    this.manager.startDeviceScan(null, null, (error, device) => {
      if (error) {
        this.error(error.message)
        return
      }
      if (device.name && device.name.indexOf('EB') > -1) {
        var indexx = -1;
        for(let i = 0; i < this.state.deviceList.length; i++) {
          if(device.name === this.state.deviceList[i].name){
            indexx = i;
          }
        }
        if(indexx === -1){
          this.setState({
            deviceList: [...this.state.deviceList, device]
          });
        }
      }
    });
  }
  
  ListViewItemSeparatorLine = () => {
    return (
      <View
        style={{
          height: .5,
          width: "100%",
          backgroundColor: "#000",
        }}
      />
    );
  }
  renderItem = ({item}) => (
    <TouchableHighlight onPress={this.OpenSecondActivity.bind(this, item)} underlayColor='gray'>
      <Text style={styles.rowViewContainer}  > {item.name} </Text>
    </TouchableHighlight>
  );
  render() {
    return (
      <View>
      <List containerStyle={{ borderTopWidth: 0, borderBottomWidth: 0 }}>
      <FlatList 
      data={this.state.deviceList}
      keyExtractor={item => item.id}
      renderItem={this.renderItem}
      ItemSeparatorComponent={this.ListViewItemSeparatorLine}
      extraData={this.state}
      />
      </List>
      <Toast ref="toast"/>
      </View>
    )
  }
  OpenSecondActivity (item) {
      if (this.state.busy) {
        return;
      }
      this.setState({ busy: true});
      this.manager.stopDeviceScan();
      this.props.navigation.navigate('Second', { ListViewClickItemHolder: item, onGoBack: (error, device) => this.refreshFunction(error, device)});
      setTimeout( () => {
        this.setState({ busy: false})
      }, 1000);
    }
    refreshFunction(error, device) {
      if(error) {
        this.refs.toast.show(error);
        var array = this.state.deviceList;
        array.remove(device);
        this.setState({deviceList: array });
      }
      if (Platform.OS === 'ios') {
        this.manager.onStateChange((state) => {
          if (state === 'PoweredOn') this.scanAndConnect()
        })
      } else {
        this.scanAndConnect()
      }
    }
  static navigationOptions =
  {
     title: 'Device Scan',
  };
}
Array.prototype.remove = function() {
  var what, a = arguments, L = a.length, ax;
  while (L && this.length) {
      what = a[--L];
      while ((ax = this.indexOf(what)) !== -1) {
          this.splice(ax, 1);
      }
  }
  return this;
};
const styles = StyleSheet.create(
  {
    rowViewContainer: 
    {
   
      fontSize: 18,
      paddingRight: 10,
      paddingTop: 10,
      paddingBottom: 10,
   
    }
   
  });