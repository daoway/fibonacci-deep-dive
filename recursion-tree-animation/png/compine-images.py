import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import numpy as np
import os

def combine_recursion_frames():
    # List of frame filenames
    frame_files = [f'frame_{i:03d}.png' for i in range(15)]
    
    # Check which files exist
    existing_frames = []
    for frame_file in frame_files:
        if os.path.exists(frame_file):
            existing_frames.append(frame_file)
        else:
            print(f"Warning: {frame_file} not found")
    
    if not existing_frames:
        print("No frame files found!")
        return
    
    num_frames = len(existing_frames)
    print(f"Found {num_frames} frames")
    
    # Calculate grid dimensions (prefer rectangular layout)
    if num_frames <= 9:
        rows, cols = 3, 3
    elif num_frames <= 12:
        rows, cols = 3, 4
    elif num_frames <= 15:
        rows, cols = 3, 5
    else:
        rows = int(np.ceil(np.sqrt(num_frames)))
        cols = int(np.ceil(num_frames / rows))
    
    # Create figure with subplots
    fig, axes = plt.subplots(rows, cols, figsize=(cols * 4, rows * 4))
    #fig.suptitle('Recursion Tree Progress', fontsize=16, fontweight='bold')
    
    # Flatten axes array for easier indexing
    if rows * cols == 1:
        axes = [axes]
    else:
        axes = axes.flatten()
    
    # Load and display each frame
    for i, frame_file in enumerate(existing_frames):
        try:
            img = mpimg.imread(frame_file)
            axes[i].imshow(img)
            axes[i].set_title(f'Step {i:03d}', fontsize=10)
            axes[i].axis('off')  # Remove axis ticks and labels
        except Exception as e:
            print(f"Error loading {frame_file}: {e}")
            axes[i].text(0.5, 0.5, f'Error loading\n{frame_file}', 
                        ha='center', va='center', transform=axes[i].transAxes)
            axes[i].axis('off')
    
    # Hide unused subplots
    for i in range(num_frames, len(axes)):
        axes[i].axis('off')
    
    # Adjust layout to prevent overlapping
    plt.tight_layout()
    
    # Save the combined image
    output_filename = 'recursion_tree_combined.png'
    plt.savefig(output_filename, dpi=300, bbox_inches='tight')
    print(f"Combined image saved as: {output_filename}")
    
    # Show the plot
    plt.show()

if __name__ == "__main__":
    combine_recursion_frames()