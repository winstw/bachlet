
const { useRef, useState, useEffect, createElement } = React
const quickAndDirtyStyle = {
  width: "200px",
  height: "200px",
  background: "#FF9900",
  color: "#FFFFFF",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  position: "absolute"
}

const DraggableComponent = (props) => {

  const [pressed, setPressed] = useState(false)
  const [position, setPosition] = useState({x: props.x ?props.x: 0, y: props.y ? props.y: 0})
  const ref = useRef()

  // Monitor changes to position state and update DOM
  useEffect(() => {
    if (ref.current) {
      ref.current.style.transform = `translate(${position.x}px, ${position.y}px)`
    }
  }, [position])

  // Update the current position if mouse is down
  const onMouseMove = (event) => {
    if (pressed) {
      setPosition({
        x: position.x + event.movementX,
        y: position.y + event.movementY
      })

    }
  }
  const onMouseUp = (event)=> {
    props.onNewPosition(position)
    setPressed(false)
  }

/*   return (
    <div
      ref={ ref }
      style={ quickAndDirtyStyle }
      onMouseMove={ onMouseMove }
      onMouseDown={ () => setPressed(true) }
      onMouseUp={ () => setPressed(false) }>
      <p>{ pressed ? "Dragging..." : "Press to drag" }</p>
    </div>
  )
 */
return React.createElement('div', {ref, style: quickAndDirtyStyle, onMouseOut: onMouseUp, onMouseMove, onMouseDown: () => setPressed(true), onMouseUp}, /*React.createElement('p', null, pressed ? "Dragging..." : "Press to drag")*/props.children)
}
/* 
export default DraggableComponent */