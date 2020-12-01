const input = document.getElementById('chat-input');
const outputArea = document.getElementById('chat-area');
const addTextButton = document.getElementById('add-text');
const getButton = document.getElementById('get');
const askButton = document.getElementById('ask');
const naskButton = document.getElementById('nask');

const socketRoute = document.getElementById('ws-route').value;
const socket = new WebSocket(socketRoute);

input.onkeydown= (event) => {
    if (event.key === 'Enter'){
        socket.send(input.value);
        input.value = '';
    }
}


addTextButton.onclick = () => socket.send('tell(textItem(t1))')

//socket.onopen = ()=>  socket.send("New user connected");
socket.onmessage = (event) => {
    outputArea.value += '\n' + event.data;
    this.data = event.data;
}
//socket.onmessage = event => this.state.items = event.data

class BachItem extends React.Component {
    constructor(props){
        super(props)
    }
    onDoubleClick = () => props.toggleEditable(this.props.id)
    onNewPosition = position => this.props.onNewItemPosition(this.props.id, position)
    render() {return React.createElement(DraggableComponent, {x: 100, y: 100, onNewPosition: this.onNewPosition}, React.createElement('p', {style: {pointerEvents: "none"}, draggable: true, onChange: (event) => console.log('CHANGED', event)}, `${this.props.value}`))
}
}

class BachWidget extends React.Component {
    constructor(props) {
        super(props);
        this.state = {items: [{id: 1, value: "a"}, {id: 2, value: "b"}]}

        props.socket.onmessage = event => {
            this.setState({items:  event.data.split(" ").map((value, id) => ({id, value}))})
            console.log(event.data, this.state.items)
        }

    }
    
    onNewItemPosition(itemId, position){
        const {x: newX, y: newY} = position
        console.log("new position")
        let item = this.state.items.find(item => item.id == itemId)
        item = {...item, x: newX, y: newY};
        console.log(this.state);
    }

    render() {
      return this.state.items.map(({id, value}) => 
//        React.createElement(Draggable, null, 
            React.createElement(BachItem, {id, value, onNewItemPosition: this.onNewItemPosition.bind(this) }, null))
    }
  }
  
  ReactDOM.render(
    React.createElement(BachWidget, {socket}, null),
    document.getElementById('root')
  );

  
