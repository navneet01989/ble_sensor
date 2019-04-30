import React, { Component } from 'react';
import {
  Text,
  View,
} from 'react-native';
import Conversion from '../Conversion';
import Spinner from 'react-native-loading-spinner-overlay';

export default class PairingScreen extends React.Component {
  constructor(props) {
    super(props)
    this.DataIndices = {
      0: "F000AA01-0451-4000-B000-000000000000",
      1: "F000AA21-0451-4000-B000-000000000000",
      2: "F000AA41-0451-4000-B000-000000000000",
      3: "F000AB21-0451-4000-B000-000000000000",
      4: "F000AB22-0451-4000-B000-000000000000",
      5: "F000AB23-0451-4000-B000-000000000000",
      6: "F000AB24-0451-4000-B000-000000000000",
      7: "F000AB25-0451-4000-B000-000000000000",
      8: "F000AB26-0451-4000-B000-000000000000",
      9: "F000AB27-0451-4000-B000-000000000000",
      10: "F000AB28-0451-4000-B000-000000000000",
      11: "F000AA81-0451-4000-B000-000000000000"
    }
    this.ServiceIndices = {
      0: "F000AA00-0451-4000-B000-000000000000",
      1: "F000AA20-0451-4000-B000-000000000000",
      2: "F000AA40-0451-4000-B000-000000000000",
      3: "F000AB20-0451-4000-B000-000000000000",
      4: "F000AB20-0451-4000-B000-000000000000",
      5: "F000AB20-0451-4000-B000-000000000000",
      6: "F000AB20-0451-4000-B000-000000000000",
      7: "F000AB20-0451-4000-B000-000000000000",
      8: "F000AB20-0451-4000-B000-000000000000",
      9: "F000AB20-0451-4000-B000-000000000000",
      10: "F000AB20-0451-4000-B000-000000000000",
      11: "F000AA80-0451-4000-B000-000000000000"
    }
    this.ConfigIndices = {
      0: "F000AA02-0451-4000-B000-000000000000",
      1: "F000AA22-0451-4000-B000-000000000000",
      2: "F000AA42-0451-4000-B000-000000000000",
      3: "F000AB29-0451-4000-B000-000000000000",
      4: "F000AB29-0451-4000-B000-000000000000",
      5: "F000AB29-0451-4000-B000-000000000000",
      6: "F000AB29-0451-4000-B000-000000000000",
      7: "F000AB29-0451-4000-B000-000000000000",
      8: "F000AB29-0451-4000-B000-000000000000",
      9: "F000AB29-0451-4000-B000-000000000000",
      10: "F000AB29-0451-4000-B000-000000000000",
      11: "F000AA82-0451-4000-B000-000000000000"
    }
    this.sensors = {
      0: "Temperature",
      1: "Humidity",
      2: "Pressure",
      3: "Fix",
      4: "Date",
      5: "Time",
      6: "Latitude",
      7: "Longitude",
      8: "Speed",
      9: "Course",
      10: "Altitude",
      11: "Gyro"
    }
    this.state = {
      info: "",
      values: {},
      visible: false,
      serviceDiscoverable: false
    }
  }
  componentWillUnmount() {
    this.props.navigation.state.params.ListViewClickItemHolder.cancelConnection()
    .then((device) => {
       
    });
  }
  componentDidMount() {
    this.setState({
      visible: true
    });
    var device = this.props.navigation.state.params.ListViewClickItemHolder;
    device.connect()
    .then((device) => {
        return device.discoverAllServicesAndCharacteristics()
    })
    .then((device) => {
       // Do work on device with services and characteristics
       this.setState({
        serviceDiscoverable: true,
        visible: false
      });
      console.log('navneet error3: ');
      this.info("Listening to " + device.name)
      this.setupNotifications(device)
    })
    .catch((error) => {
      this.setState({
        visible: false
      });
      if(error){
        console.log('navneet error1: ' + error);
        if(!this.state.serviceDiscoverable) {
          this.error('navneet error1: ' + error);
          this.props.navigation.goBack();
          this.props.navigation.state.params.onGoBack('device unavailable', this.props.navigation.state.params.ListViewClickItemHolder);
        }
      }
    });
  }
  async setupNotifications(device) {
    for(let id1 = 0; id1 < 12; id1++) {
      const service = this.ServiceIndices[id1]
      const characteristicW = this.ConfigIndices[id1]
      const characteristicN = this.DataIndices[id1]
      if(id1 === 11){
        const characteristic = await device.writeCharacteristicWithResponseForService(service, characteristicW, "/wA=" /* 0x01 in hex */);
      } else {
        const characteristic = await device.writeCharacteristicWithResponseForService(service, characteristicW, "AQ==" /* 0x01 in hex */);  
      }
      if(id1 === 0) {
        device.monitorCharacteristicForService(service, characteristicN, (error, characteristic) => {
          if (error) {
            this.error(error.message)
            return
          }
          this.updateValue(id1, this.DataIndices[id1], characteristic.value)
          for(let id = 1; id < 12; id++) {
            const characteristic1 = device.readCharacteristicForService(this.ServiceIndices[id], this.DataIndices[id])
            .then((characteristic1) => {
              // Success code
              this.updateValue(id, this.DataIndices[id], characteristic1.value)
            })
            .catch((error) => {
              // Failure code
              console.log(error);
            });
          }
        });
      }
    }
  }
  info(message) {
    this.setState({info: message})
  }

  error(message) {
    this.setState({info: "ERROR: " + message})
  }
  updateValue(index, key, value) {
    try {
      Conversion.convertGPSData(index, value, (error, data) => {
        if (error) {
          console.error(error);
        } else {
          console.log('data = : ' + key + " - - " + data);
          this.setState({values: {...this.state.values, [key]: data}})
        }
      });
    } catch (error) {
      console.error(error);
    }
  }
  render()
  {
     return(
        <View>
        <Text>{this.state.info}</Text>
        {Object.keys(this.sensors).map((key) => {
          return <Text key={key}>{this.sensors[key] + ": " + (this.state.values[this.DataIndices[key]] || "-")} </Text>
        })}
        <Spinner visible={this.state.visible} textContent={"Loading..."} textStyle={{color: '#000'}} />
      </View>
     );
  }
  static navigationOptions =
  {
     title: 'Data screen',
  };
}