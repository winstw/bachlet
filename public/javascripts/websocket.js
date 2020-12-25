const input = document.getElementById('chat-input');
const currentUser = document.getElementById('user').innerHTML.split(" ")[1];
const outputArea = document.getElementById('chat-area');
const addTextButton = document.getElementById('add-text');
const addImageButton = document.getElementById('add-image');
const addVideoButton = document.getElementById('add-video');
const deleteItem = document.getElementById('delete-item');
const getButton = document.getElementById('get');
const askButton = document.getElementById('ask');
const naskButton = document.getElementById('nask');
const socketRoute = document.getElementById('ws-route').value;


var deletedata 

/* class BachImageItem extends BachItem {
    constructor(props){
        super(props)
    }


}
 */


class BachItem extends React.Component {
    constructor(props){
        super(props)
    }
    onDoubleClick = () => props.toggleEditable(this.props.id)
    onNewPosition = position => this.props.onNewItemPosition(this.props.id, position)
    render() {

        const {type, value, user} = this.props.value;
/*         var splitedItemtemp = this.props.value.split("(",3)
        var splitedItem = splitedItemtemp[1].split("....")
        var realValue = ""
         
        realValue = splitedItem[1] ? splitedItem[1] : splitedItem[0];
        console.log(realValue);
         const itemType = splitedItemtemp[0];*/
        if (type == "user"){
            return React.createElement('i', {}, value);
        }
        else if(type =="textItem"){

            return React.createElement(DraggableComponent, {x: 100*((this.props.id+1)*2), y: 100, onNewPosition: this.onNewPosition}, 
            [React.createElement('button', {onClick: this.props.delete}, 'X'),
             React.createElement(
                'p', 
                {
                    style: {
                        pointerEvents: "none",
                        fontSize: "14px"
                    }, 
                    draggable: true, 
                    onChange: (event) => console.log('CHANGED', event)
                }
//                , `${splitedItem[2].slice(0,-1)} ${splitedItem[0]} : `),
                , `${user} : `),
                React.createElement(
                'p', 
                {
                    style: {
                        pointerEvents: "none",
                        fontSize: "28px"

                    },
                    draggable: true, 
                    onChange: (event) => console.log('CHANGED', event)
                }
                , `${value}`)]
                )
        }else if(type == "imageItem"){
            return React.createElement(DraggableComponent, {x: 100*((this.props.id+1)*2), y: 100, onNewPosition: this.onNewPosition}, 
            [React.createElement('button', {onClick: this.props.delete}, 'X'),
                React.createElement(
                'p', 
                {
                    style: {
                        pointerEvents: "none",
                        fontSize: "14px"
                    }, 
                    draggable: true, 
                    onChange: (event) => console.log('CHANGED', event)
                }
//                , `${splitedItem[2].slice(0,-1)} ${splitedItem[0]} : `),
                , `${user} : `),
                React.createElement(
                'img', 
                {
                    src: `${value}`,
                    width: "200px",
                    height: "200px"
                }
                , null)])
        }else{
            return React.createElement(DraggableComponent, {x: 100*((this.props.id+1)*2), y: 100, onNewPosition: this.onNewPosition}, 
            [React.createElement('button', {onClick: this.props.delete}, 'X'),
                React.createElement(
                'p', 
                {
                    style: {
                        pointerEvents: "none",
                        fontSize: "14px"
                    },  
                    draggable: true, 
                    onChange: (event) => console.log('CHANGED', event)
                }
//                , `${splitedItem[2].slice(0,-1)} ${splitedItem[0]} : `),
                , `${user} : `),
            React.createElement(
                'iframe', 
                {
                    src: `${value}`,
                    width: "200px",
                    height: "200px"
                }
                , null)])
        }
        
}
}

class BachWidget extends React.Component {

    initSocket(){
        const socket = new WebSocket(socketRoute);
        this.socket = socket;
        input.onkeydown= (event) => {
            if (event.key === 'Enter'){
                socket.send(input.value);
                input.value = '';
            }
        }
        socket.onopen = (event) => {
            socket.send(`tells-user-${currentUser}-${currentUser}`)
        }
            
        addTextButton.onclick = () => {
            socket.send(`tells-textItem-${currentUser}-${input.value}`)
            input.value = '';
        }
        
        addImageButton.onclick = () => {
            socket.send(`tells-imageItem-${currentUser}-${input.value}`)
            input.value = '';
        }
        
        addVideoButton.onclick = () => {
            socket.send(`tells-videoItem-${currentUser}-${input.value}`)
            input.value = '';
        }
        
/*         deleteItem.onclick = () => {
            var code = input.value.split(" ")
            if(code[0] == user || user.includes("prof")){
                deletedata = deletedata.filter(function () {return true});
                for(var i = 0;i<deletedata.length;i++){
                    var temp = deletedata[i].value.split("(")[1]
                    var typeitem
                    if(deletedata[i].value.split("(")[0].substring(0,1) == "{"){
        
                        typeitem = deletedata[i].value.split("(")[0].substring(1)
                    }else{
                        typeitem = deletedata[i].value.split("(")[0]
                    }
                    if(temp.split("....")[0] == code[1] && temp.split("....")[2].slice(0,-1) == code[0]){
                        socket.send(`get-${typeitem}-${code[1]} ${code[0]}-${temp.split("....")[1]}`)
                    }
                }
            }
            input.value = '';
        }
 */        
        socket.onmessage = event => {
            console.log("JSON", JSON.parse(event.data))
            this.setState({items:  JSON.parse(event.data)})
        }

        socket.onclose = (e)  => {
            console.log('Socket is closed. Reconnect will be attempted in 1 second.', e.reason);
            setTimeout(() =>  {
              this.initSocket();
            }, 200);
        };
    }

    constructor(props) {
        super(props);
        this.state = {items: []}
        this.initSocket();
      

    }
    onDeleteItem = ({type, value}) => () => {
        const command = `gets-${type}-${currentUser}-${value}`
        this.socket.send(command)
    }
    
    onNewItemPosition(itemId, position){
        const {x: newX, y: newY} = position
        console.log("new position")
        let item = this.state.items.find(item => item.id == itemId)
        item = {...item, x: newX, y: newY};
        console.log(this.state);
    }

    render() {
        deletedata = this.state.items
        console.log("Les items : ", this.state.items)
        
/*          for(var i = 0;i<this.state.items.length;i++){
            console.log("It : ", this.state.items[i].value)
            if(!user.includes("prof")){
                console.log("Je suis là")
                console.log("=>", this.state.items[i].value.split("....")[2].slice(0,-5))
                if(!(this.state.items[i].value.split("....")[2].includes(user)) && !(this.state.items[i].value.split("....")[2].includes("prof"))){
                    console.log("ok")
                    delete this.state.items[i]
                }
            }
        } */
 
        
        console.log("Les items 2: ", this.state.items)

        return this.state.items.map((value, id) => 
          React.createElement(BachItem, {id, value, delete: this.onDeleteItem(value), onNewItemPosition: this.onNewItemPosition.bind(this) }, null));
    
    }
}
  
ReactDOM.render(
    React.createElement(BachWidget, {}, null),
    document.getElementById('root')
    );

  

  
